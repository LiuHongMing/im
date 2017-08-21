package com.im.netty.codec;

import com.fasterxml.aalto.AsyncByteBufferFeeder;
import com.fasterxml.aalto.AsyncXMLInputFactory;
import com.fasterxml.aalto.AsyncXMLStreamReader;
import com.fasterxml.aalto.evt.EventAllocatorImpl;
import com.fasterxml.aalto.stax.InputFactoryImpl;
import com.im.netty.xml.XMLEventObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.XMLEvent;
import java.nio.ByteBuffer;
import java.util.List;

/**
 * Processes socket bytes into XML Events.
 */
public class XMLFrameDecoder extends ByteToMessageDecoder {

    private final static Logger logger = LoggerFactory.getLogger(XMLFrameDecoder.class);

    private static final AsyncXMLInputFactory XML_INPUT_FACTORY = new InputFactoryImpl();
    private static final EventAllocatorImpl EVENT_ALLOCATOR = EventAllocatorImpl.getDefaultInstance();

    private AsyncXMLStreamReader<AsyncByteBufferFeeder> streamReader;

    static {
        XML_INPUT_FACTORY.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
    }

    public XMLFrameDecoder() {
        streamReader = XML_INPUT_FACTORY.createAsyncForByteBuffer();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("decode buffer={}, channel={}", in, ctx.channel());
        }

        if (in.isReadable()) {
            byte[] chunk = new byte[in.readableBytes()];
            in.readBytes(chunk);
            AsyncByteBufferFeeder inputFeeder = null;
            try {
                ByteBuffer byteBuffer = ByteBuffer.wrap(chunk);
                streamReader.getInputFeeder().feedInput(byteBuffer);
                XMLEventObject xmlEventObject = new XMLEventObject();
                while (streamReader.hasNext()) {
                    int token = streamReader.next();
                    XMLEvent xmlEvent = EVENT_ALLOCATOR.allocate(streamReader);
                    if (token == AsyncXMLStreamReader.EVENT_INCOMPLETE) {
                        xmlEventObject.addEvent(xmlEvent);
                        out.add(xmlEventObject);
                        break;
                    }
                    xmlEventObject.addEvent(xmlEvent);
                }
            } catch (Exception e) {
                logger.error("xml格式异常", e);
                streamReader = XML_INPUT_FACTORY.createAsyncForByteBuffer();
                logger.info("streamReader = XML_INPUT_FACTORY.createAsyncForByteBuffer()");
            } finally {

            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("out={}", out);
        }
    }
}
