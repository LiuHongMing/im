package com.im.xmpp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log4j2Demo {

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger("log2kafka");
        logger.info("测试");
    }

}
