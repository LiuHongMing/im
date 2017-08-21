package com.im.xmpp;

import com.im.netty.auth.Token;
import io.netty.channel.DefaultChannelId;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetDemo {

    public static void main(String[] args) throws UnknownHostException {
        System.out.println((InetAddress.getLocalHost()).getHostName());
        Token token = new Token("1", "im.cinyi.com");
        System.out.println(token);

        DefaultChannelId channelId = DefaultChannelId.newInstance();
        System.out.println(channelId.asLongText() + "," + channelId.asShortText());
        System.out.println(ToStringBuilder.reflectionToString(channelId, ToStringStyle.SHORT_PREFIX_STYLE));
    }

}
