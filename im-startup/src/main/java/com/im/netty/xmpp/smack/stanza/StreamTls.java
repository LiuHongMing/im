package com.im.netty.xmpp.smack.stanza;

import org.jivesoftware.smack.packet.StartTls;

public class StreamTls extends StartTls {

    public static String PROCEED = "<proceed xmlns=\"urn:ietf:params:xml:ns:xmpp-tls\"/>";

    public StreamTls() {
        this(false);
    }

    public StreamTls(boolean required) {
        super(required);
    }

}
