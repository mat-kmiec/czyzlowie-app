package pl.czyzlowie.modules.fish_forecast.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Configuration class for customizing the Jackson {@link ObjectMapper}.
 * This class is annotated with {@link Configuration}, indicating that
 * it is a source of Spring bean definitions.
 */
@Configuration
public class JacksonConfig {

    /**
     * Configures and provides a primary {@link ObjectMapper} bean for the application.
     * The returned ObjectMapper is customized to:
     * - Register the {@link JavaTimeModule} for Java 8 date and time API support.
     * - Disable the serialization feature that writes dates as timestamps.
     *
     * @return an instance of {@link ObjectMapper} configured for the application's needs.
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
