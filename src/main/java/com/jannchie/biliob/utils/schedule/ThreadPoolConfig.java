package com.jannchie.biliob.utils.schedule;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author Pan Jianqi
 */
@Configuration
@EnableAsync
public class ThreadPoolConfig {

    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor singleThreadPool = new ThreadPoolTaskExecutor();
        singleThreadPool.setCorePoolSize(20);
        singleThreadPool.setMaxPoolSize(20);
        singleThreadPool.setQueueCapacity(20);
        singleThreadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        singleThreadPool.initialize();
        return singleThreadPool;
    }

    @Bean("singleThreadPool")
    public ThreadPoolTaskExecutor singleThreadPool() {
        ThreadPoolTaskExecutor singleThreadPool = new ThreadPoolTaskExecutor();
        singleThreadPool.setCorePoolSize(1);
        singleThreadPool.setMaxPoolSize(1);
        singleThreadPool.setQueueCapacity(1);
        singleThreadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        singleThreadPool.initialize();
        return singleThreadPool;
    }
}