package com.im.netty.server;

/**
 * IM服务接口
 *
 * @author jason
 */
public interface ImServer {

    /**
     * 启动
     */
    void start();

    /**
     * 重启
     */
    void restart();

    /**
     * 停止
     */
    void shutdown();
}
