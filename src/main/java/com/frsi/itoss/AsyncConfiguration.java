package com.frsi.itoss;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync

public class AsyncConfiguration implements AsyncConfigurer {
    @Value("${itoss.async.corePoolSize:0}")
    private int corePoolSize;

    @Value("${itoss.async.maxPoolSize:0}")
    private int maxPoolSize;

    @Value("${itoss.async.queueCapacity:0}")
    private int queueCapacity;

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new CustomAsyncExceptionHandler();
    }

    @Bean("taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        if (corePoolSize > 0) executor.setCorePoolSize(corePoolSize);
        if (maxPoolSize > 0) executor.setMaxPoolSize(maxPoolSize);
        if (queueCapacity > 0) executor.setQueueCapacity(queueCapacity);
        //executor.setKeepAliveSeconds(5);
        executor.setThreadNamePrefix("ItossAsyncTasks-");

        executor.initialize();
        return executor;
    }

}
