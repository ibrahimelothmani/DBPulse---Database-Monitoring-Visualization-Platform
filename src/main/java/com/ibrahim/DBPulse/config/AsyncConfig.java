package com.ibrahim.DBPulse.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Async configuration for asynchronous task execution.
 * Useful for background processing, email notifications, etc.
 */
@Configuration
@EnableAsync
@Slf4j
public class AsyncConfig {

    /**
     * Configure thread pool for async task execution.
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        log.info("Creating Async Task Executor");

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // Core pool size - threads always kept alive
        executor.setCorePoolSize(5);

        // Maximum pool size - max threads to create
        executor.setMaxPoolSize(10);

        // Queue capacity - tasks waiting for thread
        executor.setQueueCapacity(100);

        // Thread name prefix for easier debugging
        executor.setThreadNamePrefix("async-");

        // Rejection policy - what to do when queue is full
        executor.setRejectedExecutionHandler((r, exec) -> {
            log.warn("Task rejected: {}", r.toString());
        });

        // Wait for tasks to complete on shutdown
        executor.setWaitForTasksToCompleteOnShutdown(true);

        // Max wait time for task completion on shutdown (seconds)
        executor.setAwaitTerminationSeconds(60);

        executor.initialize();

        return executor;
    }
}
