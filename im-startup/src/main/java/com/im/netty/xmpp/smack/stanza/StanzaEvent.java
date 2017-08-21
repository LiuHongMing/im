package com.im.netty.xmpp.smack.stanza;

import com.google.common.eventbus.Subscribe;
import com.im.netty.session.Connection;
import com.im.netty.session.ConnectionManager;
import com.im.netty.xmpp.handler.NegotiationHandler;
import io.netty.channel.Channel;
import org.jivesoftware.smack.packet.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StanzaEvent {

    private static final Logger logger = LoggerFactory.getLogger(NegotiationHandler.class);

    private ConnectionManager connectManager;

    public StanzaEvent(ConnectionManager connectManager) {
        this.connectManager = connectManager;
    }

    @Subscribe
    public void message(Message message) {
        logger.info("StanzaEvent[message] -> {}", message.toXML().toString());

        String id   = message.getStanzaId();
        String to   = message.getTo();
        Message.Type type = message.getType();

        Connection toConnection = connectManager.get(to);
        if (toConnection != null) {
            Channel toChannel = toConnection.getChannel();
            if (type == Message.Type.chat) {
                toChannel.writeAndFlush(message.toXML().toString());
            }
        }
    }

}
