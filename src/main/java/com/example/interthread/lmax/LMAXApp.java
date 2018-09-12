package com.example.interthread.lmax;

import com.example.interthread.LongEvent;
import com.example.interthread.NamedThreadFactory;
import com.example.interthread.lmax.LongEventFactory;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.nio.ByteBuffer;

public class LMAXApp {

    private static final int BUFFER_SIZE = 1024;
    private static final long NR_EVENTS = 500_000_000;

    public static void main(String[] args) {
        LongEventFactory eventFactory = new LongEventFactory();

        // Initialize the disruptor
        Disruptor<LongEvent> disruptor = new Disruptor<>(eventFactory, BUFFER_SIZE, new NamedThreadFactory(), ProducerType.SINGLE, new BusySpinWaitStrategy());

        // Attach the (single) consumer
        disruptor.handleEventsWith(new EventHandler<LongEvent>() {

            @Override
            public void onEvent(LongEvent event, long sequence, boolean endOfBatch) throws Exception {

            }
        });

        // Start the disruptor; this would start the consumer's thread
        disruptor.start();

        // Get a reference to the ring buffer for the Producer to be able to begin generating messages
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

        ByteBuffer byteBuffer = ByteBuffer.allocate(8);

        long start = System.nanoTime();
        for (long num=0; num<NR_EVENTS; num++) {
            byteBuffer.putLong(0, num);

            // Publish events by the Producer
            ringBuffer.publishEvent(new EventTranslatorOneArg<LongEvent, ByteBuffer>() {

                @Override
                public void translateTo(LongEvent event, long sequence, ByteBuffer bb) {
                    event.setValue(byteBuffer.getLong(0));
                }

            }, byteBuffer);
        }

        System.out.println("Elapsed time: " + ((double)(System.nanoTime()) - (double)start) / 1000000 + " ms");

        disruptor.shutdown();
    }
}
