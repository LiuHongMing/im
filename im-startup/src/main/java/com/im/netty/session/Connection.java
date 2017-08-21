package com.im.netty.session;

import com.google.common.base.Preconditions;
import com.im.netty.auth.Token;
import io.netty.channel.Channel;

import java.io.Serializable;

public class Connection implements Serializable {

    private String id;
    private Channel channel;
    private Token token;

    public Connection(String id, Channel channel) {
        this.id = Preconditions.checkNotNull(id);
        this.channel = channel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return getId();
    }
}
