package io.netty.demo;

import io.netty.util.Recycler;
import io.netty.util.internal.RecyclableArrayList;

public class RecycleArrayList {

    private static final Recycler<RecycleArrayList> RECYCLY = new Recycler<RecycleArrayList>() {
        @Override
        protected RecycleArrayList newObject(Handle<RecycleArrayList> handle) {
            return new RecycleArrayList(handle);
        }
    };

    private final Recycler.Handle<RecycleArrayList> handle;

    public RecycleArrayList(Recycler.Handle<RecycleArrayList> handle) {
        this.handle = handle;
    }

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
        System.out.println(RecycleArrayList.class.getResource("/"));
    }
}
