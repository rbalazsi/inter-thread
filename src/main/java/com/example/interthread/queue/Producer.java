package com.example.interthread.queue;

import com.example.interthread.LongEvent;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class Producer implements Runnable {

    private BlockingQueue<LongEvent> queue;
    private long nrEvents;
    private CountDownLatch latch;

    public Producer(BlockingQueue<LongEvent> queue, long nrEvents, CountDownLatch latch) {
        this.queue = queue;
        this.nrEvents = nrEvents;
        this.latch = latch;
    }

    @Override
    public void run() {
        for (int i=0; i<nrEvents; i++) {
            LongEvent event = new LongEvent();
            event.setValue(i);
            try {
                queue.put(event);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        latch.countDown();
    }
}
