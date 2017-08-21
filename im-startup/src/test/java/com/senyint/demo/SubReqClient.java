package com.senyint.demo;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import java.nio.charset.Charset;

public class SubReqClient {

    private static final Range<Integer> allowingPortRange = Range.closed(80, 65535);
    private static final Integer DEFAULT_LISTENING_PORT = 80;
    private static final Charset DEFAULT_CHARSET = Charset.defaultCharset();

    private String host;
    //服务端口
    private int port ;
    //内容字符编码
    private String charset;

    public SubReqClient(String host) {
        this(host, DEFAULT_LISTENING_PORT, DEFAULT_CHARSET.toString());
    }

    public SubReqClient(String host, int port) {
        this(host, port, DEFAULT_CHARSET.toString());
    }

    public SubReqClient(String host, int port, String charset) {
        Preconditions.checkArgument(allowingPortRange.contains(port), "port(%s) is out of range %s", port, allowingPortRange);
        this.host = host;
        this.port = port;
        this.charset = charset;
    }

    public void connction() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new ProtobufVarint32FrameDecoder())
                                    .addLast(new ProtobufDecoder(SubscribeRespProto.SubscribeResp.getDefaultInstance()))
                                    .addLast(new ProtobufVarint32LengthFieldPrepender())
                                    .addLast(new ProtobufEncoder())
                                    .addLast(new SubReqClientHandler());
                        }
                    });
            ChannelFuture f = bootstrap.connect(host, port).sync();
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        Integer port = 8080;
        SubReqClient client = new SubReqClient("127.0.0.1", port);
        try {
            client.connction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
