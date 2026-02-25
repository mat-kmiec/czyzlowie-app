package pl.czyzlowie.modules.imgw_api.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "imgw.api")
@Value
@Validated
public class ImgwApiProperties {

    @NotBlank
    String meteoUrl;

    @NotBlank
    String synopUrl;

    @NotBlank
    String hydroUrl;
}
