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

import static org.springframework.security.authorization.AuthorityAuthorizationManager.hasRole;
import static org.springframework.security.authorization.AuthorizationManagers.not;
import static org.springframework.security.authorization.AuthorizationManagers.allOf;


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
                        .requestMatchers(HttpMethod.POST, "/api/payments")
                        .access(allOf(
                                        hasRole("STUDENT"),
                                        not(hasRole("TUTOR"))
                                ))
                        .requestMatchers(HttpMethod.GET, "/api/payments/methods").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/payments/{transactionId}/bank-transfer").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/payments/{transactionId}/credit-card").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/payments/history").hasRole("STAFF")
                        .requestMatchers(HttpMethod.GET, "/api/payments/{transactionId}").hasRole("STAFF")
                        .requestMatchers(HttpMethod.POST, "/api/payments/{transactionId}/refund").access(allOf(
                                        hasRole("STUDENT"),
                                        not(hasRole("TUTOR"))
                                ))
                        .anyRequest().authenticated()
                );
        
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
