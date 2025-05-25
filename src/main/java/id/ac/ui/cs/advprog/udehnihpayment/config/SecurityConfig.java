package id.ac.ui.cs.advprog.udehnihpayment.config;

import id.ac.ui.cs.advprog.udehnihpayment.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        
                        // ========== ENDPOINTS DENGAN API KEY VALIDATION (permitAll) ==========
                        // Course Service API - Create Payment
                        .requestMatchers(HttpMethod.POST, "/api/payments").permitAll()
                        
                        // Dashboard Service API - Get all data
                        .requestMatchers(HttpMethod.GET, "/api/payments/transactions").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/payments/refunds").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/payments/refunds/*").permitAll()
                        
                        // Dashboard Service API - Update status
                        .requestMatchers(HttpMethod.PUT, "/api/payments/*/status").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/payments/refunds/*/status").permitAll()
                        
                        // Mixed Authentication - API Key OR JWT
                        .requestMatchers(HttpMethod.GET, "/api/payments/*").permitAll()
                        
                        // ========== PUBLIC ENDPOINTS (No Authentication) ==========
                        .requestMatchers(HttpMethod.GET, "/api/payments/methods").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/payments/process").permitAll()
                        
                        // ========== JWT AUTHENTICATION REQUIRED ==========
                        // Student endpoints
                        .requestMatchers(HttpMethod.GET, "/api/payments/history").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.POST, "/api/payments/*/bank-transfer").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.POST, "/api/payments/*/credit-card").hasRole("STUDENT")
                        .requestMatchers(HttpMethod.POST, "/api/payments/*/refund").hasRole("STUDENT")
                        
                        // ========== FALLBACK ==========
                        .anyRequest().authenticated()
                );
        
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}