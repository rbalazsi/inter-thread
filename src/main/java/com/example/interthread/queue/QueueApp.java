package com.example.interthread.queue;

import com.example.interthread.LongEvent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class QueueApp {

    private static final int BUFFER_SIZE = 1024 * 1024;
//    private static final long NR_EVENTS = 500_000_000;
private static final long NR_EVENTS = 500_000_000;

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<LongEvent> eventQueue = new ArrayBlockingQueue<>(BUFFER_SIZE);
        CountDownLatch latch = new CountDownLatch(1);
//        Producer producer = new Producer(eventQueue, NR_EVENTS, latch);
        Consumer consumer = new Consumer(eventQueue, NR_EVENTS, latch);
//        new Thread(producer).start();
        new Thread(consumer).start();

        long start = System.nanoTime();

        for (int i=0; i<NR_EVENTS; i++) {
            LongEvent event = new LongEvent();
            event.setValue(i);
            try {
                eventQueue.put(event);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        latch.await();
        System.out.println("Elapsed time: " + ((double)(System.nanoTime()) - (double)start) / 1000000 + " ms");
    }
}
