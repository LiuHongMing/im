package com.im.xmpp;

import com.google.common.base.Preconditions;
import com.im.netty.xmpp.Constants;
import com.im.netty.xmpp.smack.stanza.Stream;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.StartTls;
import org.jivesoftware.smack.util.XmlStringBuilder;
import org.springframework.util.StringUtils;

import java.util.concurrent.ExecutionException;

public class StanzaDemo {

    public static void main(String[] args) throws ExecutionException {
//        StreamOpen streamOpen = new StreamOpen("", "to", StanzaIdUtil.newStanzaId(),
//                "en", StreamOpen.StreamContentNamespace.server);
//        System.out.println(streamOpen.toXML());
//
//        StreamElement streamElement = new StreamElement(Constants.DOMAIN, "1");
//        System.out.println(streamElement.toXML());

//        System.out.println(JID.jid("domain", "local", "resource"));
//        String uri = "user@domain/resource";
//        System.out.println(JID.jid(uri));
//
//        System.out.println(new SimpleIQ() {
//        });

        // starttls
//        StartTls startTls = new StartTls(true);
//        System.out.println(startTls.toXML());
//
//        StreamElement.StreamFeatures streamFeatures = new StreamElement.StreamFeatures();
//        XmlStringBuilder builder = new XmlStringBuilder();
//        builder.append(streamOpen.toXML())
//                .element(streamFeatures)
//                .element(startTls)
//                .closeElement(streamFeatures);
//        System.out.println(builder.toString());

//        QName sf = new QName("stream:feature");
//        System.out.println(sf.toString());

//        Presence presence = new Presence(Presence.Type.available, "1", 1, Presence.Mode.chat);
//        System.out.println(presence.toXML().toString());
//
//        Presence presence2 = new Presence(Presence.Type.available);
//        System.out.println(presence2.toXML().toString());
//
//        Element presenceElement = XMLUtil.fromString("<presence><show>xa</show></presence>");
//        System.out.println(presenceElement.getTagName());

//        String result = tlsNegotiated();
//        System.out.println(result);

//        Presence presence = new Presence(Presence.Type.available);
//        presence.setFrom(null);

        Message message = new Message("im.cinyi.com", Message.Type.chat);
        System.out.println(message.toXML().toString());

    }

    private static boolean starttls = true;

    private static String streamOpen(String to, String from) {
        Preconditions.checkNotNull(to);

        Stream streamElement;
        if (StringUtils.isEmpty(from)) {
            streamElement = new Stream(to);
        } else {
            streamElement = new Stream(to, from);
        }
        return streamElement.toXML().toString();
    }

    private static String starttls() {
        StartTls startTls = new StartTls(true);
        return startTls.toXML().toString();
    }

    private static String streamFeature() {
        XmlStringBuilder builder = new XmlStringBuilder();
        Stream.StreamFeatures streamFeatures = new Stream.StreamFeatures();
        builder.element(streamFeatures);
        if (starttls) {
            builder.append(starttls());
        }
        builder.closeElement(streamFeatures);
        return builder.toString();
    }

    private static String tlsNegotiated() {
        XmlStringBuilder builder = new XmlStringBuilder();
        builder.append(streamOpen(Constants.DOMAIN, null));
        builder.append(streamFeature());
        return builder.toString();
    }

}
