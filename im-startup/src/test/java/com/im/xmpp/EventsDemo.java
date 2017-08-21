package com.im.xmpp;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.im.netty.server.NettyUtil;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.internal.SystemPropertyUtil;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.packet.StreamOpen;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class EventsDemo {

    static class Message {
        String content;

        public Message(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    static class SimpleIQ extends IQ {

        private String attribute;
        private String element;

        protected SimpleIQ(String childElementName, String childElementNamespace, String attribute, String element) {
            super(childElementName, childElementNamespace);
            this.attribute = attribute;
            this.element = element;
        }

        @Override
        protected IQChildElementXmlStringBuilder getIQChildElementBuilder(IQChildElementXmlStringBuilder xml) {
            xml.attribute("attribute", attribute);
            xml.rightAngleBracket();
            xml.element("value", element);
            return xml;
        }
    }

    static class StanzaEvent {

        @Subscribe
        public void response(Stanza stanza) {
            System.out.println(stanza.toXML());
        }

        @Subscribe
        public void send(Message message) {
            System.out.println(message.getContent());
        }

    }

    public static void main(String[] args) {
        int nThread = NettyUtil.nThread();
        ThreadFactory threadFactory = NettyUtil.threadFactory("eventBus-worker");
        Executor executor = Executors.newFixedThreadPool(nThread, threadFactory);
        EventBus eventBus = new AsyncEventBus(executor);
        eventBus.register(new StanzaEvent());
        eventBus.post(new Message("<message to='foo'><body/></message>"));

        Presence presence = new Presence(Presence.Type.subscribe);
        eventBus.post(presence);

        eventBus.post(new SimpleIQ("query", "emcc.jiyq", "what", "elemeng"));
    }

}
