package com.im.netty.xmpp.smack.stanza;

import com.im.netty.utils.XMLUtil;
import com.im.netty.xmpp.SimpleElement;
import org.apache.commons.lang3.StringUtils;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SimplePresence implements SimpleElement {

    private Presence presence;

    public SimplePresence(Element element) {
        config(element);
    }

    private void config(Element element) {
        this.presence = new Presence(Presence.Type.available);
        // attributes
        String to = element.getAttribute("to");
        if (!StringUtils.isEmpty(to)) {
            presence.setTo(to);
        }
        String from = element.getAttribute("from");
        if (!StringUtils.isEmpty(from)) {
            presence.setFrom(from);
        }
        String id = element.getAttribute("id");
        if (!StringUtils.isEmpty(id)) {
            presence.setStanzaId(id);
        }
        String type = element.getAttribute("type");
        if (!StringUtils.isEmpty(type)) {
            presence.setType(Presence.Type.valueOf(type));
        }
        String lang = element.getAttribute("xml:lang");
        if (!StringUtils.isEmpty(lang)) {
            presence.setLanguage(lang);
        }
        // extensions
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node node = nodeList.item(i);
            presence.addExtension(new SimpleExtension() {
                @Override
                public CharSequence toXML() {
                    return XMLUtil.toString(node);
                }
            });
        }
    }

    @Override
    public Stanza toStanza() {
        return presence;
    }

    public static void main(String[] args) {
        Element element = XMLUtil.fromString("<presence type='subscribe'>" +
                "<status>Listen to the music</status>" +
                "<show>chat</show>" +
                "</presence>");
        SimplePresence presence = new SimplePresence(element);
        System.out.println(presence.toStanza().toXML().toString());
    }

}
