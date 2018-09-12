package com.example.interthread.queue;

import com.example.interthread.LongEvent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class Consumer implements Runnable {

    private BlockingQueue<LongEvent> queue;
    private long nrEvents;
    private CountDownLatch latch;

    public Consumer(BlockingQueue<LongEvent> queue, long nrEvents, CountDownLatch latch) {
        this.queue = queue;
        this.nrEvents = nrEvents;
        this.latch = latch;
    }

    @Override
    public void run() {
        for (int i=0; i<nrEvents; i++) {
            try {
                LongEvent event = queue.take();
                System.out.println("Consumed event with sequence " + event.getValue());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        latch.countDown();
    }
}
