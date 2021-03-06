package com.example.interthread;

import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

public class BenchmarkRunner {

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include("com.example.interthread.*")
                .forks(1)
                .build();

        new Runner(options).run();
    }
}
