package com.im.netty.xmpp.handler;

import com.google.common.eventbus.EventBus;
import com.im.netty.session.ConnectionManager;
import com.im.netty.utils.EventBusUtil;
import com.im.netty.xmpp.smack.stanza.StanzaEvent;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ChannelHandler.Sharable
public class StanzaHandler extends SimpleChannelInboundHandler<Stanza> {

    private static final Logger logger = LoggerFactory.getLogger(NegotiationHandler.class);

    private static final EventBus eventBus = EventBusUtil.DEFAULT;

    static {
        eventBus.register(new StanzaEvent(ConnectionManager.getInstance()));
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, Stanza stanza) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("receive stanza=[{}]", stanza);
        }
        eventBus.post(stanza);
    }
}
