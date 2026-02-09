package pl.czyzlowie.modules.moon.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "astronomy.api")
public class AstronomyProperties {
    private String url;
    private String appId;
    private String appSecret;
}
