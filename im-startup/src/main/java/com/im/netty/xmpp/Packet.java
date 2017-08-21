package com.im.netty.xmpp;

import com.im.netty.xml.XMLElement;
import com.im.netty.utils.XMLUtil;
import com.im.netty.xmpp.smack.stanza.StanzaBuilder;
import org.jivesoftware.smack.packet.Stanza;
import org.w3c.dom.Element;

public class Packet {

    public static Stanza fromElement(XMLElement xmlElement) {
        String tagName = xmlElement.getTagName();
        Element domElement = XMLUtil.fromString(xmlElement.toString());

        Stanza stanza = null;
        if (Constants.MESSAGE.equals(tagName)) {
            stanza = StanzaBuilder.message(domElement);
        }

        return stanza;
    }

}
