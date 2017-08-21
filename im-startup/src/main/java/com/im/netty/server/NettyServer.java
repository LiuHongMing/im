package com.im.netty.server;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;
import com.im.netty.codec.XMLElementDecoder;
import com.im.netty.codec.XMLFrameDecoder;
import com.im.netty.handler.IdleStateAwareHandler;
import com.im.netty.session.ConnectionManager;
import com.im.netty.xmpp.handler.NegotiationHandler;
import com.im.netty.xmpp.handler.StanzaHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * Netty服务类
 *
 * @author jason
 */
public class NettyServer extends ServerConfig implements ImServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private static final Range<Integer> allowingPortRange = Range.closed(80, 65535);
    private static final int DEFAULT_PORT = 8666;

    private String ip = "0.0.0.0";
    private int port = DEFAULT_PORT;
    private ServerBootstrap serverBootstrap;

    private EventLoopGroup boosGroup;
    private EventLoopGroup workerGroup;
    private Class<? extends ServerChannel> channelClass;

    private ChannelFuture channelFuture;

    private ConnectionManager connectManager = ConnectionManager.getInstance();

    public NettyServer() {
        this(DEFAULT_PORT);
    }

    public NettyServer(int port) {
        Preconditions.checkArgument(allowingPortRange.contains(port), "port(%s) is out of range %s", port, allowingPortRange);
        this.port = port;
    }

    public static NettyServer newInstance() {
        return newInstance(DEFAULT_PORT);
    }

    public static NettyServer newInstance(int port) {
        return new NettyServer(port);
    }

    @Override
    public void start() {
        serverBootstrap = new ServerBootstrap();

        // init groups and channelClass
        if (isUseLinuxEpoll()) {
            boosGroup   = new EpollEventLoopGroup(getBossThreads(), new DefaultThreadFactory("epollServer-boss"));
            workerGroup = new EpollEventLoopGroup(getWorkThreads(), new DefaultThreadFactory("epollServer-worker"));
            serverBootstrap.group(boosGroup, workerGroup);
            channelClass = EpollServerSocketChannel.class;
            serverBootstrap.channel(channelClass);
        } else {
            boosGroup   = new NioEventLoopGroup(getBossThreads(), new DefaultThreadFactory("nioServer-boss"));
            workerGroup = new NioEventLoopGroup(getWorkThreads(), new DefaultThreadFactory("nioServer-worker"));
            serverBootstrap.group(boosGroup, workerGroup);
            channelClass = NioServerSocketChannel.class;
            serverBootstrap.channel(channelClass);
        }

        serverBootstrap.handler(new LoggingHandler(LogLevel.INFO));

        // parent options
        serverBootstrap.option(ChannelOption.SO_BACKLOG, 128);
        serverBootstrap.option(ChannelOption.TCP_NODELAY, true);
        serverBootstrap.option(ChannelOption.SO_KEEPALIVE, true);
        serverBootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

        // child options
        serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        serverBootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

        // init channelHandler
        serverBootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ChannelPipeline pipeline = ch.pipeline();
                // -------------------
                // 传输层 outbound
                // -------------------
                pipeline.addLast("logger", new LoggingHandler(LogLevel.INFO));

                // ssl
                SslHandler sslHandler = sslHandler(ch.alloc());
                if (sslHandler != null) {
                    pipeline.addLast("ssl", sslHandler);
                }

                // idle, 空闲超时10分钟
                pipeline.addLast("idleHandler", new IdleStateHandler(0, 0, 10, TimeUnit.MINUTES));
                pipeline.addLast("idleStateAwareHandler", new IdleStateAwareHandler());

                // encode
                pipeline.addLast("stringEncoder", new StringEncoder(Charset.forName("UTF-8")));

                // xml
                pipeline.addLast("xmlFrameDecoder",   new XMLFrameDecoder());
                pipeline.addLast("xmlElementDecoder", new XMLElementDecoder());

                // xmpp
                pipeline.addLast("xmppNegotiation", new NegotiationHandler(NettyServer.this));
                pipeline.addLast("xmppStanza",    new StanzaHandler());

                // -------------------
                // 应用层 inbound
                // -------------------
            }
        });
        try {
            channelFuture = serverBootstrap.bind(port).addListeners(new FutureListener<Void>() {
                @Override
                public void operationComplete(Future<Void> future) throws Exception {
                    if (future.isSuccess()) {
                        if (logger.isInfoEnabled()) {
                            logger.info("start succeed in ip:port => {}:{}", ip, port);
                        }
                    }
                }
            });
        } catch (Exception e) {
            logger.error("start failed on ip:port => {}:{}", ip, port, e);
        } finally {
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    shutdown();
                }
            });
        }
    }

    @Override
    public void restart() {
        shutdown();
        start();
    }

    @Override
    public void shutdown() {
        if (channelFuture != null) {
            channelFuture.channel().close().syncUninterruptibly();
        }
        if (boosGroup != null) {
            boosGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        if (logger.isInfoEnabled()) {
            logger.info("shutdown completed ...");
        }
    }

    public String ip() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int port() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
