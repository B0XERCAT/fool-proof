//  CorsConfig.java
//  This class is for CORS in security filters.
// ================================================
package com.foolproof.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


public class FilterCorsConfig {

    // FRONTEND_URL is injected from local environment variable `SPRING_FRONTEND_URL`
    private final String FRONTEND_URL;

    public FilterCorsConfig(@Value("${SPRING_FRONTEND_URL}") String frontendUrl) {
        FRONTEND_URL = frontendUrl;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
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
}