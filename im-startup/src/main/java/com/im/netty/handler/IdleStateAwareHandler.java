package com.im.netty.handler;

import com.im.netty.session.ConnectionManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Processes timeout when channel idle.
 */
@ChannelHandler.Sharable
public class IdleStateAwareHandler extends ChannelDuplexHandler {

    private static final Logger logger = LoggerFactory.getLogger(IdleStateAwareHandler.class);

    private ConnectionManager connectionManager;

    public IdleStateAwareHandler() {}

    public IdleStateAwareHandler(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            Channel channel = ctx.channel();
            IdleState idleState = ((IdleStateEvent) evt).state();
            switch (idleState) {
                case READER_IDLE:
                    // read timeout
                    logger.warn("channel=[{}] read timeout will close.", channel);
                    channel.close();
                    break;
                case WRITER_IDLE:
                    // write timeout
                    logger.warn("channel=[{}] write timeout will close.", channel);
                    channel.close();
                    break;
                case ALL_IDLE:
                    // idle timeout
                    logger.warn("channel=[{}] idle timeout will close.", channel);
                    channel.close();
                    break;
                default:
                    break;
            }
        }
    }
}
