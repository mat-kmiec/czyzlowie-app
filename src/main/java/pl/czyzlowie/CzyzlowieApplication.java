package pl.czyzlowie;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@ConfigurationPropertiesScan("pl.czyzlowie.modules.imgw.config")
public class CzyzlowieApplication {

    public static void main(String[] args) {
        SpringApplication.run(CzyzlowieApplication.class, args);
    }

}
