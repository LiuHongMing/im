package com.im.netty.xmpp;

import org.jivesoftware.smack.packet.Stanza;

public interface SimpleElement {

    Stanza toStanza();

}
