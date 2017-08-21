package com.im.netty.client;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Scanner;

public class ChatClient {

    private static final Range<Integer> allowingPortRange = Range.closed(80, 65535);
    private static final Integer DEFAULT_LISTENING_PORT = 8666;

    private String ip;
    //服务端口
    private int port;

    public boolean connected;

    public ChatClient(String ip) {
        this(ip, DEFAULT_LISTENING_PORT);
    }

    public ChatClient(String ip, int port) {
        Preconditions.checkArgument(allowingPortRange.contains(port), "port(%s) is out of range %s", port, allowingPortRange);
        this.ip = ip;
        this.port = port;
    }

    public boolean isConnected() {
        return connected;
    }

    private ChatClientHandler chatClientHandler;

    public void connction() throws Exception {
        boolean SSL = System.getProperty("ssl") != null;
        // Configure SSL.
        final SslContext sslCtx;
        if (SSL) {
            sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }

        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            chatClientHandler = new ChatClientHandler("chat" + RandomStringUtils.randomNumeric(2));
                            ChannelPipeline pipeline = ch.pipeline();
                            if (sslCtx != null) {
                                pipeline.addLast(sslCtx.newHandler(ch.alloc()));
                            }
                            pipeline.addLast(new StringDecoder())
                                    .addLast(new StringEncoder())
                                    .addLast(chatClientHandler);
                        }
                    });
            ChannelFuture f = bootstrap.connect(ip, port).sync();
            connected = true;
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        final ChatClient client = new ChatClient("127.0.0.1");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.connction();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String message = scanner.nextLine();
            if (client.connected) {
                client.input(message);
            }
        }
    }

    /**
     * @param message 格式: tagName from to
     */
    private void input(String message) {
        String[] params = message.split(" ");
        String tagName = params[0];
        String from = params[1];
        String to = "toUser";
        if (params.length > 2) {
            to = params[2];
        }

        if ("s".equals(tagName)) {
            message = "<stream:stream from='" + from + "@im.cinyi.com' to='im.cinyi.com' " +
                    "xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams'>";
        }

        if ("m".equals(tagName)) {
            String msg = "Hi";
            if (params.length > 3) {
                msg = params[3];
            }
            message = "<message id='ktx72v49' " +
                    " from='" + from + "@im.cinyi.com' to='" + to + "@im.cinyi.com' " +
                    " type='chat' xml:lang='en'><body>" + msg + "</body>" +
                    "</message>";
        }

        chatClientHandler.say(message);
    }

}
