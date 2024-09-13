//  CorsConfig.java
//  This class is for CORS in security filters.
// ================================================
package com.foolproof.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class FilterCorsConfig {

    // FRONTEND_URL is injected from local environment variable `SPRING_FRONTEND_URL`
    private final String FRONTEND_URL;

    public FilterCorsConfig(@Value("${SPRING_FRONTEND_URL}") String frontendUrl) {
        FRONTEND_URL = frontendUrl;
    }

    @Bean
    @Primary
    // mvcHandlerMappingIntrospector is auto configured and registed as bean.
    // Thus, two beans conflict making Spring impossible to decide which one to inject into SecurityConfig.
    // Hence, making the folllowing method needs to be annotated as Primary.
    public CorsConfigurationSource corsConfigurationSource() {
        System.out.println("Here: " + FRONTEND_URL);

        // Set up CORS configuration below.
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin(FRONTEND_URL); // Allow cors with frontend.
        configuration.addAllowedHeader("*"); // Allow all HTTP headers.
        configuration.addAllowedMethod("*"); // Allow all HTTP methods.
        configuration.setAllowCredentials(true); // Allow credentials like cookie.

        // Apply the configuration to a URL path
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply to all paths
        return source;
    }

    public String temp() {
        return FRONTEND_URL;
    }
}