package com.mentoria.agil.backend.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private SecurityFilter securityFilter;
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                       
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()
                        
                        .requestMatchers(HttpMethod.POST, "/api/mentors/**").hasRole("MENTOR")
                        .requestMatchers(HttpMethod.PUT, "/api/mentors/**").hasRole("MENTOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/mentors/**").hasAnyRole("MENTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/mentors/**").authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/users/mentores").authenticated()
                        
                        .requestMatchers(HttpMethod.POST, "/api/mentorias/request").hasRole("ESTUDANTE")
                        .requestMatchers(HttpMethod.GET, "/api/mentorias/historico").hasRole("ESTUDANTE")
                        .requestMatchers(HttpMethod.GET, "/api/mentorias/pendentes").hasRole("MENTOR")
                        .requestMatchers(HttpMethod.PATCH, "/api/mentorias/**").hasRole("MENTOR")

                        .requestMatchers(HttpMethod.POST, "/api/disponibilidades").hasRole("MENTOR")
                        .requestMatchers(HttpMethod.GET, "/api/disponibilidades/mentor/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/sessoes/agendar").hasRole("ESTUDANTE")

                        .requestMatchers(HttpMethod.POST, "/api/sessoes/**/feedback").hasRole("ESTUDANTE")
                        .requestMatchers(HttpMethod.POST, "/api/sessoes/**/materiais/**").hasRole("MENTOR")

                        .requestMatchers(HttpMethod.POST, "/api/materiais").hasRole("MENTOR")
                        .requestMatchers(HttpMethod.GET, "/api/materiais/meus-materiais").hasRole("ESTUDANTE")
                        
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                    .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200", "http://localhost:8080"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L); 
    
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}