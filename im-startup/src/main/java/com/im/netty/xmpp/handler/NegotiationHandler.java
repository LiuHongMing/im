package com.im.netty.xmpp.handler;

import com.google.common.base.Preconditions;
import com.im.netty.auth.TokenBuilder;
import com.im.netty.session.ConnectionManager;
import com.im.netty.session.Connection;
import com.im.netty.ssl.SslConfig;
import com.im.netty.xml.XMLElement;
import com.im.netty.xmpp.Constants;
import com.im.netty.xmpp.Packet;
import com.im.netty.xmpp.exception.StreamOpenException;
import com.im.netty.xmpp.smack.stanza.StreamTls;
import com.im.netty.xmpp.smack.stanza.Stream;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.ssl.SslHandler;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.StartTls;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class NegotiationHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(NegotiationHandler.class);

    enum StreamOption {
        CONNECT, STARTTLS, TLSCONNECT, AUTHENTICATE, READY, DISCONNECT
    }

    private SslConfig sslConfig;
    private boolean starttls = true;

    private ConnectionManager connectionManager = ConnectionManager.getInstance();

    private StreamOption option = StreamOption.DISCONNECT;

    public NegotiationHandler(SslConfig sslConfig) {
        this.sslConfig = sslConfig;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        option = StreamOption.CONNECT;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("receive msg=[{}]", msg);
        }

        if (msg instanceof XMLElement) {
            Channel channel = ctx.channel();

            XMLElement element = (XMLElement) msg;
            String tagName = element.getTagName();
            switch (option) {
                case CONNECT:
                    if (Constants.STREAM_STREAM.equals(tagName)) {
                        String to = element.getAttribute("to");
                        String from = element.getAttribute("from");
                        try {
                            String streamOpen = streamOpen(from, to);
                            channel.writeAndFlush(streamOpen);
                        } catch (Exception e) {
                            throw new StreamOpenException(e.getCause());
                        }
                        if (from == null) {
                            from = channel.id().asShortText();
                        }
                        Connection connection = new Connection(from, channel);
                        connection.setToken(TokenBuilder.token(from));
                        connectionManager.add(connection);

                        option = StreamOption.STARTTLS;

                        if (logger.isDebugEnabled())
                            logger.debug("client connect ok. channel=[{}]", channel);
                    }
                    break;
                case STARTTLS:
                    if (Constants.STREAM_START_TLS.equals(tagName)) {
                        String proceed = StreamTls.PROCEED;
                        channel.writeAndFlush(proceed);

                        ChannelHandlerContext sslCtx = channel.pipeline().context(SslHandler.class);
                        if (sslCtx == null) {
                            channel.pipeline().addFirst("tls", sslConfig.sslHandler(ctx.alloc()));
                        }
                        option = StreamOption.TLSCONNECT;
                        ctx.fireChannelRead(msg);
                        if (logger.isDebugEnabled())
                            logger.debug("client start tls ok. channel=[{}]", channel);
                        break;
                    }
                {
                    logger.debug("client request msg by channel=[{}]", channel);
                    Stanza stanza = Packet.fromElement(element);
                    if (stanza == null)
                        throw new Exception("unknow stanza.");
                    ctx.fireChannelRead(stanza);
                    option = StreamOption.READY;
                    break;
                }
                case TLSCONNECT:
                    if (Constants.STREAM_STREAM.equals(element.getTagName())) {
                        option = StreamOption.READY;
                        // 返回client端
                        channel.writeAndFlush(tlsNegotiated());
                        if (logger.isDebugEnabled()) {
                            logger.debug("client tls connect ok. channel=[{}]",
                                    channel);
                        }
                    }
                    break;
                case READY:
                    logger.debug("client request msg by channel=[{}]", channel);
                    Stanza stanza = Packet.fromElement(element);
                    if (stanza == null)
                        throw new Exception("unknow stanza.");
                    ctx.fireChannelRead(stanza);
                    break;
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        option = StreamOption.DISCONNECT;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage(), cause);
    }

    private String streamOpen(String to, String from) {
        Preconditions.checkNotNull(to);

        Stream streamElement;
        if (StringUtils.isEmpty(from)) {
            streamElement = new Stream(to);
        } else {
            streamElement = new Stream(to, from);
        }
        return streamElement.toXML().toString();
    }

    private String streamFeature() {
        XmlStringBuilder builder = new XmlStringBuilder();
        Stream.StreamFeatures streamFeatures = new Stream.StreamFeatures();
        builder.element(streamFeatures);
        if (starttls) {
            builder.append(starttls());
        }
        builder.closeElement(streamFeatures);
        return builder.toString();
    }

    private String starttls() {
        StartTls startTls = new StartTls(true);
        return startTls.toXML().toString();
    }

    private String tlsNegotiated() {
        XmlStringBuilder builder = new XmlStringBuilder();
        builder.append(streamOpen(Constants.DOMAIN, null));
        builder.append(streamFeature());
        return builder.toString();
    }

}
