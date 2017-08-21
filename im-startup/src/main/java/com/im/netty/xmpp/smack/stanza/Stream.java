package com.im.netty.xmpp.smack.stanza;

import com.im.netty.utils.UUIDGenerator;
import org.jivesoftware.smack.packet.NamedElement;
import org.jivesoftware.smack.packet.StreamOpen;
import org.jivesoftware.smack.util.XmlStringBuilder;

public class Stream extends StreamOpen {

    public Stream(CharSequence to) {
        this(to, null);
    }

    public Stream(CharSequence to, CharSequence from) {
        super(to, from, UUIDGenerator.getUUID32());
    }

    public Stream(CharSequence to, CharSequence from, String id, String lang, StreamContentNamespace ns) {
        super(to, from, id, lang, ns);
    }

    public static class StreamFeatures implements NamedElement {

        public static final String ELEMENT = "stream:feature";

        @Override
        public String getElementName() {
            return ELEMENT;
        }

        @Override
        public CharSequence toXML() {
            XmlStringBuilder xml = new XmlStringBuilder(this);
            xml.rightAngleBracket();
            return xml;
        }
    }
}
