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
    public static class QueueBenchmarkState {

        @Param(value = {/*"1000", */"10000"/*, "100000", "10000000", "50000000", "500000000"*/})
        long nrEvents;

        private CountDownLatch latch;
        private BlockingQueue<LongEvent> queue;

        @Setup(Level.Trial)
        public void setUp() {
            System.out.println("Scenario set up.");
            queue = new ArrayBlockingQueue<>(BUFFER_SIZE);
            latch = new CountDownLatch(1);
            new Thread(new Consumer(queue, nrEvents, latch)).start();
        }

        @TearDown(Level.Trial)
        public void tearDown() {
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 1)
    @Measurement(iterations = 5)
    @Fork(value = 1)
    public void queueAverageTime(QueueBenchmarkState state) throws InterruptedException {
        System.out.println("Scenario started.");
        for (int i=0; i<state.nrEvents; i++) {
            LongEvent event = new LongEvent();
            event.setValue(i);
            try {
                state.queue.put(event);
                System.out.println("Event produced with sequence " + i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        state.latch.await();
    }
}
