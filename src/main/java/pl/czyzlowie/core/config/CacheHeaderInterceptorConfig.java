package pl.czyzlowie.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheHeaderInterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CacheHeaderInterceptor());
    }

    public static class CacheHeaderInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler) {
            String path = request.getRequestURI();

            if (path.matches(".*\\.(css|js|png|jpg|jpeg|gif|svg|woff2?|ttf|ico|webp)$")) {
                CacheControl cc = CacheControl.maxAge(365, TimeUnit.DAYS)
                        .cachePublic()
                        .immutable();
                response.setHeader("Cache-Control", cc.getHeaderValue());
                response.setHeader("Pragma", "cache");
                response.setHeader("Expires", "Wed, 30 Dec 2026 23:59:59 GMT");
            }
            else if (path.endsWith(".html") || path.matches("^/$") || path.matches("^/.*/$")) {
                CacheControl cc = CacheControl.maxAge(1, TimeUnit.HOURS)
                        .cachePublic()
                        .mustRevalidate();
                response.setHeader("Cache-Control", cc.getHeaderValue());
            }
            else if (path.startsWith("/api/")) {
                CacheControl cc = CacheControl.noCache()
                        .cachePrivate()
                        .mustRevalidate();
                response.setHeader("Cache-Control", cc.getHeaderValue());
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Expires", "0");
            }
            else if (path.equals("/sw.js") || path.equals("/manifest.json")) {
                CacheControl cc = CacheControl.maxAge(1, TimeUnit.HOURS)
                        .cachePublic()
                        .mustRevalidate();
                response.setHeader("Cache-Control", cc.getHeaderValue());
            }

            response.setHeader("X-Content-Type-Options", "nosniff");
            response.setHeader("X-Frame-Options", "SAMEORIGIN");
            response.setHeader("X-XSS-Protection", "1; mode=block");

            return true;
        }
    }
}





