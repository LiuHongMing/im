package com.im.xmpp;

import com.fasterxml.aalto.AsyncByteBufferFeeder;
import com.fasterxml.aalto.AsyncXMLInputFactory;
import com.fasterxml.aalto.AsyncXMLStreamReader;
import com.fasterxml.aalto.evt.EventAllocatorImpl;
import com.fasterxml.aalto.stax.InputFactoryImpl;
import com.fasterxml.aalto.stax.OutputFactoryImpl;
import com.im.netty.xml.XMLElement;
import com.im.netty.xml.XMLElementImpl;
import com.im.netty.xml.XMLEventObject;
import com.im.netty.utils.XMLUtil;
import com.im.netty.xmpp.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.dom.DOMResult;
import java.nio.ByteBuffer;
import java.util.List;

public class AaltoDemo {

    public static void main(String[] args) throws XMLStreamException {
        EventAllocatorImpl ALLOCATOR = EventAllocatorImpl.getDefaultInstance();

        AsyncXMLInputFactory FACTORY = new InputFactoryImpl();
        FACTORY.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);

        AsyncXMLStreamReader<AsyncByteBufferFeeder> streamReader = FACTORY.createAsyncForByteBuffer();
        streamReader.getInputFeeder().feedInput(ByteBuffer.wrap("<stream:stream xmlns:stream='http://etherx.jabber.org/streams'><presence/></stream:stream>".getBytes()));

        XMLEventObject xmlEventObject = new XMLEventObject();
        while (streamReader.hasNext()) {
            int token = streamReader.next();
            XMLEvent xmlEvent = ALLOCATOR.allocate(streamReader);
            if (token == AsyncXMLStreamReader.EVENT_INCOMPLETE) {
                xmlEventObject.addEvent(xmlEvent);
                break;
            }
            xmlEventObject.addEvent(xmlEvent);
        }

        if (xmlEventObject.isEmpty()) {
            return;
        }

        XMLOutputFactory XML_OUTPUT_FACTORY = new OutputFactoryImpl();

        Document document = XMLUtil.newDocument();
        DOMResult result = new DOMResult(document);
        XMLEventWriter writer = XML_OUTPUT_FACTORY.createXMLEventWriter(result);
        if (null == writer || null == document) {
            return;
        }

        XMLElement xmlElement = null;

        List<XMLEvent> events = xmlEventObject.events();
        for (XMLEvent event : events) {
            try {
                if (null != writer)
                    writer.add(event);
            } catch (Exception e) {
            }

            if (event.getEventType() == AsyncXMLStreamReader.EVENT_INCOMPLETE) {
                try {
                    if (null != writer) {
                        writer.flush();
                    }
                    if (null != document) {
                        Element element = document.getDocumentElement();
                        xmlElement = XMLElementImpl.fromElement(element);
                    }
                } catch (Exception e) {
                } finally {
                    if (null != writer) {
                        writer.close();
                        writer = null;
                    }
                    if (null != document) {
                        document = null;
                    }
                }
            }
        }

        if (xmlElement != null) {
            xmlElement.setAttribute("to", Constants.DOMAIN);
        }

        System.out.println(xmlElement);
    }

}
