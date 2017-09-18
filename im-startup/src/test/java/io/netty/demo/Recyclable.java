package io.netty.demo;

import io.netty.util.internal.RecyclableArrayList;

public class Recyclable {

    public static void main(String[] args) throws InterruptedException {
        int i = 0, times = 1000000;
        byte[] data = new byte[1024];
        while (i++ < times) {
            RecyclableArrayList list = RecyclableArrayList.newInstance();
            int count = 100;
            for (int j = 0; j < count; j++) {
                list.add(data);
            }
            list.recycle();
            System.out.println("count:[" + count + "]");
            Thread.sleep(1);
        }
        System.out.println(Recyclable.class.getResource("/"));
    }
}
