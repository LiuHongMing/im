package com.im.netty.utils;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.im.netty.server.NettyUtil;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public final class EventBusUtil {

    public static final EventBus DEFAULT;

    static {
        int nThread = NettyUtil.nThread();
        ThreadFactory threadFactory = NettyUtil.threadFactory("eventBus-worker");
        Executor executor = Executors.newFixedThreadPool(nThread, threadFactory);
        DEFAULT = new AsyncEventBus(executor);
    }

    public static EventBus register(Object object) {
        DEFAULT.register(object);
        return DEFAULT;
    }
}
