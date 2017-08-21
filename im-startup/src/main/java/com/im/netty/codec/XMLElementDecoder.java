package com.im.netty.codec;

import com.fasterxml.aalto.AsyncXMLStreamReader;
import com.fasterxml.aalto.stax.OutputFactoryImpl;
import com.im.netty.xml.XMLElement;
import com.im.netty.xml.XMLElementImpl;
import com.im.netty.xml.XMLEventObject;
import com.im.netty.utils.XMLUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.dom.DOMResult;
import java.util.List;

/**
 * Processes XML Events into XML Elements.
 */
public class XMLElementDecoder extends MessageToMessageDecoder {

    private static final Logger logger = LoggerFactory.getLogger(XMLElementDecoder.class);

    private static final XMLOutputFactory XML_OUTPUT_FACTORY = new OutputFactoryImpl();

    @Override
    protected void decode(ChannelHandlerContext ctx, Object msg, List out) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("receive msg=[{}], channel=[{}]",
                    new Object[]{msg, ctx.channel()});
        }

        if (msg instanceof XMLEventObject) {
            XMLEventObject xmlEventObject = (XMLEventObject) msg;
            if (xmlEventObject.isEmpty()) {
                logger.warn("xmleventobject is empty.");
                return;
            }

            Document document = XMLUtil.newDocument();
            DOMResult result = new DOMResult(document);
            XMLEventWriter writer = XML_OUTPUT_FACTORY
                    .createXMLEventWriter(result);
            if (null == writer || null == document) {
                logger.warn("writer is null or document is null.");
                return;
            }

            List<XMLEvent> events = xmlEventObject.events();
            for (XMLEvent event : events) {
                try {
                    if (null != writer)
                        writer.add(event);
                } catch (Exception e) {
                    logger.error("writer is null or document is null.", e);
                }

                if (event.getEventType() == AsyncXMLStreamReader.EVENT_INCOMPLETE) {
                    try {
                        if (null != writer) {
                            writer.flush();
                        }
                        if (null != document) {
                            Element element = document.getDocumentElement();
                            XMLElement xmlElement = XMLElementImpl.fromElement(element);
                            ctx.fireChannelRead(xmlElement);
                        }
                    } catch (Exception e) {
                         logger.error("writer.flush() or document has exception.", e);
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
        }
    }
}
