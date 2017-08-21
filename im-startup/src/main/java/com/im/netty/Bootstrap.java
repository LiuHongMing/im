package com.im.netty;

import com.im.netty.server.ImServer;
import com.im.netty.server.NettyServer;

public class Bootstrap {

    public static void main(String[] args) throws InterruptedException {
        ImServer server = NettyServer.newInstance();
        server.start();
    }

}
