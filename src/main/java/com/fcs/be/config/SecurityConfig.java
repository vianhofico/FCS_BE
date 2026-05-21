package com.fcs.be.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler;

    public SecurityConfig(
        JwtAuthenticationFilter jwtAuthenticationFilter,
        OAuth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.oauth2AuthenticationSuccessHandler = oauth2AuthenticationSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/v1/health",
                    "/api/v1/auth/**",
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/ws",
                    "/ws/**",
                    "/api/v1/payments/webhook",
                    "/api/webhook/payos"
                ).permitAll()
                .requestMatchers(org.springframework.http.HttpMethod.GET,
                    "/api/v1/products",
                    "/api/v1/products/*",
                    "/api/v1/products/*/categories",
                    "/api/v1/products/*/reviews",
                    "/api/v1/products/*/reviews/summary",
                    "/api/v1/catalog/categories",
                    "/api/v1/catalog/categories/*",
                    "/api/v1/catalog/brands",
                    "/api/v1/catalog/brands/*",
                    "/api/v1/media"
                ).permitAll()
                .requestMatchers("/api/v1/iam/roles/**", "/api/v1/iam/permissions/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/iam/users/*/addresses", "/api/v1/iam/users/addresses/**").authenticated()
                .requestMatchers("/api/v1/iam/users/**").hasAnyRole("ADMIN", "MANAGER")
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2.successHandler(oauth2AuthenticationSuccessHandler))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
