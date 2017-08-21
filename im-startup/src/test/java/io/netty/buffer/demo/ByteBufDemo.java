package io.netty.buffer.demo;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.util.CharsetUtil;

public class ByteBufDemo {

    public static void main(String[] args) {
        System.setProperty("io.netty.allocator.type", "unpooled");
        ByteBuf byteBuf = ByteBufAllocator.DEFAULT.buffer();
        byteBuf.writeBytes("测试".getBytes(CharsetUtil.UTF_8));
        String hexDump = ByteBufUtil.prettyHexDump(byteBuf);
        System.out.println(hexDump);
    }

}
