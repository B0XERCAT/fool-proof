package com.foolproof.global.config;

import static java.util.Arrays.asList;
import static java.util.Collections.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private Environment env;

    private final String FRONTEND_URL;

    public SecurityConfig(@Value("${SPRING_FRONTEND_URL}") String frontendUrl) {
        FRONTEND_URL = frontendUrl;
    }

    @Bean   
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF disable
            .csrf((auth) -> auth
                .disable()
            )
            // Allow cross-origin resource sharing with front end.
            .cors(cors -> cors               
                .configurationSource(corsConfigurationSource())
            )
            // Disable form login method
            .formLogin((auth) -> auth
                .disable()
            )
            // Disable http basic authentication method
            .httpBasic((auth) -> auth
                .disable()
            )
            // Set session as stateless
            .sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            // Authorize by path
            .authorizeHttpRequests(
                (auth) -> auth
                    .requestMatchers("/").authenticated()
                    .requestMatchers("/api/**").permitAll()
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            );

        // http
        //     .formLogin(
        //         (auth) -> auth
        //             .loginPage( + "/temp/login")
        //             .loginProcessingUrl("/api/login")
        //             .permitAll()
        //             .defaultSuccessUrl()A
        //     );

        return http.build();
    }

    protected CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",
                getDefaultCorsConfiguration());

        return source;
    }

    private CorsConfiguration getDefaultCorsConfiguration() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin(FRONTEND_URL);
        config.addAllowedHeader("*"); //모든 종류의 HTTP 헤더를 허용하도록 설정
        config.addAllowedMethod("*"); //모든 종류의 HTTP 메소드를 허용하도록 설정

        return config;
    }

}