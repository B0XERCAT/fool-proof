package com.foolproof.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.foolproof.global.jwt.JWTFilter;
import com.foolproof.global.jwt.JWTUtil;
import com.foolproof.global.jwt.LoginFilter;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private Environment env;

    private final String FRONTEND_URL;
    private final JWTUtil jwtUtil;
    private final AuthenticationConfiguration authenticationConfiguration;

    public SecurityConfig(
        @Value("${SPRING_FRONTEND_URL}") String frontendUrl, 
        AuthenticationConfiguration authenticationConfiguration,
        JWTUtil jwtUtil
    ) {
        this.FRONTEND_URL = frontendUrl;
        this.authenticationConfiguration = authenticationConfiguration;
        this.jwtUtil = jwtUtil;
    }

    @Bean   
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        LoginFilter loginFilter = new LoginFilter(
            authenticationManager(authenticationConfiguration), 
            jwtUtil
        );

        JWTFilter jwtFilter = new JWTFilter(jwtUtil);

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
            // Add custom UsernamePasswordAuthenticationFilter due to disabling form login method
            .addFilterAt(
                loginFilter,
                UsernamePasswordAuthenticationFilter.class
            )
            // Add JWT validation filter
            .addFilterBefore(
               jwtFilter,
                LoginFilter.class
            )
            // Authorize by path
            .authorizeHttpRequests(
                (auth) -> auth
                    .requestMatchers("/").authenticated()
                    .requestMatchers("/login", "/join").permitAll()
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            );

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