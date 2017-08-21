package com.im.netty.session;

import com.google.common.collect.Maps;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

public class ConnectionManager {

    private static final ConnectionManager INSTANCE = new ConnectionManager();

    private final ConcurrentMap<String, Connection> connections = Maps.newConcurrentMap();

    private String domain;

    private ConnectionManager() {
    }

    private ConnectionManager(String domain) {
        this.domain = domain;
    }

    public void add(Connection connection) {
        String id = connection.getId();
        if (!connections.containsKey(id)) {
            connections.put(id, connection);
        }
    }

    public Collection<Connection> connections() {
        return connections.values();
    }

    public Connection get(String id) {
        return connections.get(id);
    }

    public void remove(String id) {
        connections.remove(id);
    }

    public static ConnectionManager getInstance() {
        return INSTANCE;
    }
}
