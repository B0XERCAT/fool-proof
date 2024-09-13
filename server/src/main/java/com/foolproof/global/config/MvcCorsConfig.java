//    WebConfig.java
//    This class is for CORS in controllers.
// =====================================================
package com.foolproof.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcCorsConfig {

    // FRONTEND_URL is injected from local environment variable `SPRING_FRONTEND_URL`
    private final String FRONTEND_URL;

    public MvcCorsConfig(@Value("${SPRING_FRONTEND_URL}") String frontendUrl) {
        FRONTEND_URL = frontendUrl;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        // Set up CORS configuration below.
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("*")
                    .allowedOrigins(FRONTEND_URL)
                    .allowedMethods("*")
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600);
            }
        };
    }
}
