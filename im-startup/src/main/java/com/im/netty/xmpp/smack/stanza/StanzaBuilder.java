package com.im.netty.xmpp.smack.stanza;

import org.jivesoftware.smack.packet.Stanza;
import org.w3c.dom.Element;

public class StanzaBuilder {

    public static Stanza presence(Element element) {
        return new SimplePresence(element).toStanza();
    }

    public static Stanza message(Element element) {
        return new SimpleMessage(element).toStanza();
    }

}
