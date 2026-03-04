package pl.czyzlowie.modules.fish_forecast.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuration class for enabling asynchronous task execution
 * and defining thread pool executors. This class is annotated
 * with @Configuration to indicate that it is a source of
 * Spring bean definitions, and with @EnableAsync to enable
 * asynchronous processing.
 */
@Configuration
@EnableAsync
public class FishForecastAsyncConfig {

    /**
     * Creates and configures an Executor bean named "locationExecutor".
     * Configures a ThreadPoolTaskExecutor with a core pool size of 3,
     * a maximum pool size of 5, and a thread name prefix of "Loc-".
     *
     * @return an Executor instance configured with a thread pool for handling tasks.
     */
    @Bean(name = "locationExecutor")
    public Executor locationExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(5);
        executor.setThreadNamePrefix("Loc-");
        executor.initialize();
        return executor;
    }

    /**
     * Creates and configures a ThreadPoolTaskExecutor bean named "dataFetchExecutor".
     * This executor is designed to handle asynchronous tasks with a core pool size of 10,
     * a maximum pool size of 20, and threads prefixed with "DataFetch-".
     *
     * @return an instance of Executor for asynchronous task execution
     */
    @Bean(name = "dataFetchExecutor")
    public Executor dataFetchExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setThreadNamePrefix("DataFetch-");
        executor.initialize();
        return executor;
    }
}
