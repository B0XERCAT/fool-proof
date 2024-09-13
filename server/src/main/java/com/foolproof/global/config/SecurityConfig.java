package com.foolproof.global.config;

import com.foolproof.global.jwt.JwtUtil;
import com.foolproof.global.jwt.RefreshTokenRepository;
import com.foolproof.global.jwt.filter.JwtFilter;
import com.foolproof.global.jwt.filter.CustomLoginFilter;
import com.foolproof.global.jwt.filter.CustomLogoutFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final JwtUtil jwtUtil;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final CorsConfigurationSource corsConfig;
    private final RefreshTokenRepository refreshTokenRepository;

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
        CustomLoginFilter loginFilter = new CustomLoginFilter(
            authenticationManager(authenticationConfiguration),
            jwtUtil,
            refreshTokenRepository
        );

        CustomLogoutFilter logoutFilter = new CustomLogoutFilter(
            jwtUtil, 
            refreshTokenRepository
        );

        JwtFilter jwtFilter = new JwtFilter(jwtUtil);

        http
            // CSRF disable
            .csrf((auth) -> auth
                .disable()
            )
            // Allow cross-origin resource sharing with front end.
            .cors(cors -> cors               
                .configurationSource(corsConfig)
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
                CustomLoginFilter.class
            )
            // Add Logout filter
            .addFilterBefore(
                logoutFilter,
                 LogoutFilter.class
            )
            // Authorize by path
            .authorizeHttpRequests(
                (auth) -> auth
                    .requestMatchers("/").authenticated()
                    .requestMatchers("/login", "/join", "/reissue").permitAll()
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            );

        return http.build();
    }
}