package pl.czyzlowie.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebResourceCacheConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/")
                .setCachePeriod(31536000)
                .resourceChain(true);

        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/")
                .setCachePeriod(31536000)
                .resourceChain(true);

        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/")
                .setCachePeriod(31536000)
                .resourceChain(true);

        registry.addResourceHandler("/sw.js")
                .addResourceLocations("classpath:/static/sw.js")
                .setCachePeriod(3600) // 1 godzina
                .resourceChain(true);

        registry.addResourceHandler("/manifest.json")
                .addResourceLocations("classpath:/static/manifest.json")
                .setCachePeriod(3600)
                .resourceChain(true);

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
                .setCachePeriod(31536000)
                .resourceChain(true);
    }
}

