package pl.czyzlowie.modules.forecast.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuration class for weather forecasting application.
 *
 * This class configures and initializes a thread pool task executor
 * named "weatherExecutor" for handling asynchronous tasks related to
 * weather processing. It utilizes the Spring Framework annotations
 * to enable asynchronous capabilities and task scheduling.
 *
 * Annotations:
 * - @Configuration: Indicates that this class is a source of
 *   bean definitions.
 * - @EnableAsync: Enables Spring's asynchronous method execution capability.
 * - @EnableScheduling: Enables scheduling of tasks within
 *   the application context.
 *
 * Methods:
 * - weatherExecutor(): Configures and provides a thread pool task
 *   executor with specific properties such as core pool size,
 *   maximum pool size, queue capacity, and thread name prefix.
 */
@Configuration
@EnableAsync
@EnableScheduling
public class ForecastConfig {
    @Bean(name = "weatherExecutor")
    public Executor weatherExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("Meteo-");
        executor.initialize();
        return executor;
    }

}
