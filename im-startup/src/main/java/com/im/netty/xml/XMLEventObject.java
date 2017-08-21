package com.im.netty.xml;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.xml.stream.events.XMLEvent;
import java.util.ArrayList;
import java.util.List;

public class XMLEventObject {

    private final List<XMLEvent> events = new ArrayList<XMLEvent>();

    public final List<XMLEvent> events() {
        return events;
    }

    public final void addEvent(XMLEvent event) {
        this.events.add(event);
    }

    public final boolean isEmpty() {
        return (null == this.events ? true : (this.events.size() == 0 ? true
                : false));
    }

    @Override
    public final String toString() {
        return ToStringBuilder.reflectionToString(this,
                ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
