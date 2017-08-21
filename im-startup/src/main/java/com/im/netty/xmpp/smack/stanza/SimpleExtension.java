package com.im.netty.xmpp.smack.stanza;

import org.jivesoftware.smack.packet.ExtensionElement;

public abstract class SimpleExtension implements ExtensionElement {

    @Override
    public String getNamespace() {
        return null;
    }

    @Override
    public String getElementName() {
        return null;
    }
}
