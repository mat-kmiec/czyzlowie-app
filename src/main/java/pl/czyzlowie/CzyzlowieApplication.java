package pl.czyzlowie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@ConfigurationPropertiesScan("pl.czyzlowie.modules.imgw.config")
public class CzyzlowieApplication {

    public static void main(String[] args) {

        SpringApplication.run(CzyzlowieApplication.class, args);


    }

}
