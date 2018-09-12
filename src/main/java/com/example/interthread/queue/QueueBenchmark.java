package com.example.interthread.queue;

import com.example.interthread.LongEvent;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class QueueBenchmark {

    private static final int BUFFER_SIZE = 1024 * 1024;

    @State(Scope.Benchmark)
    public static class QueueAverageTimeBenchmarkState {

        @Param(value = {"1000", "10000", "100000", "10000000", "50000000", "500000000"})
        long nrEvents;

        CountDownLatch latch;
        BlockingQueue<LongEvent> queue;

        @Setup(Level.Invocation)
        public void setUp() {
            queue = new ArrayBlockingQueue<>(BUFFER_SIZE);
            latch = new CountDownLatch(1);
            new Thread(new Consumer(queue, nrEvents, latch)).start();
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            queue.clear();
        }
    }

    @State(Scope.Benchmark)
    public static class QueueThroughputBenchmarkState {

        CountDownLatch latch;
        BlockingQueue<LongEvent> queue;

        @Setup(Level.Invocation)
        public void setUp() {
            queue = new ArrayBlockingQueue<>(BUFFER_SIZE);
            latch = new CountDownLatch(1);
            new Thread(new Consumer(queue, 1, latch)).start();
        }

        @TearDown(Level.Invocation)
        public void tearDown() {
            queue.clear();
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 1, time = 2000, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 5, time = 2000, timeUnit = TimeUnit.MILLISECONDS)
    @Fork(value = 1)
    public void queueAverageTime(QueueAverageTimeBenchmarkState state) throws InterruptedException {
        for (int i=0; i<state.nrEvents; i++) {
            LongEvent event = new LongEvent();
            event.setValue(i);
            try {
                state.queue.put(event);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        state.latch.await();
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 5, time = 2000, timeUnit = TimeUnit.MILLISECONDS)
    @Measurement(iterations = 5, time = 2000, timeUnit = TimeUnit.MILLISECONDS)
    @Fork(value = 1)
    public void queueThroughput(QueueThroughputBenchmarkState state) throws InterruptedException {
        LongEvent event = new LongEvent();
        event.setValue(7);
        state.queue.put(event);

        state.latch.await();
    }
}
