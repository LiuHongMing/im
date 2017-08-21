package com.im.netty.xmpp.smack.stanza;

import com.im.netty.utils.XMLUtil;
import com.im.netty.xmpp.SimpleElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SimpleMessage implements SimpleElement {

    private Message message;

    public SimpleMessage(Element element) {
        config(element);
    }

    private void config(Element element) {
        this.message = new Message();
        // attributes
        String to = element.getAttribute("to");
        if (!StringUtils.isEmpty(to)) {
            message.setTo(to);
        }
        String from = element.getAttribute("from");
        if (!StringUtils.isEmpty(from)) {
            message.setFrom(from);
        }
        String id = element.getAttribute("id");
        if (!StringUtils.isEmpty(id)) {
            message.setStanzaId(id);
        }
        String type = element.getAttribute("type");
        if (!StringUtils.isEmpty(type)) {
            message.setType(Message.Type.valueOf(type));
        }
        String lang = element.getAttribute("xml:lang");
        if (!StringUtils.isEmpty(lang)) {
            message.setLanguage(lang);
        }
        // extensions
        NodeList nodeList = element.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node node = nodeList.item(i);
            message.addExtension(new SimpleExtension() {
                @Override
                public CharSequence toXML() {
                    return XMLUtil.toString(node);
                }
            });
        }
    }

    @Override
    public Stanza toStanza() {
        return message;
    }

    public static void main(String[] args) {
        Element element = XMLUtil.fromString("<message to='im.cinyi.com' xml:lang='en'>" +
                "<body>Hi</body></message>");
        SimpleMessage message = new SimpleMessage(element);
        System.out.println(message.toStanza().toXML().toString());
    }

}
