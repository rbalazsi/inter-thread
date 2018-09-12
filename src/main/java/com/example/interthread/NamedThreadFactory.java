package com.example.interthread;

import java.util.concurrent.ThreadFactory;

public class NamedThreadFactory implements ThreadFactory {

    private int counter;

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable, "ConsumerThread_" + counter);
        counter++;
        return thread;
    }
}
