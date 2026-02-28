package pl.czyzlowie.modules.fish_forecast.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class FishForecastAsyncConfig {

    @Bean(name = "locationExecutor")
    public Executor locationExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3);
        executor.setMaxPoolSize(5);
        executor.setThreadNamePrefix("Loc-");
        executor.initialize();
        return executor;
    }

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
