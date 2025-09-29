package com.copo.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Disable CSRF for form login simplicity
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login/**","/**", "/css/**", "/js/**", "/images/**").permitAll()
             // Student access: only these endpoints
                .requestMatchers(
                    "/student-marks",
                    "/student-marks/**","/**",
                    "/subjects/by-dept-sem"
                ).hasAnyRole("STUDENT", "FACULTY")

                // Faculty has access to everything
                .anyRequest().hasRole("FACULTY")
                //.anyRequest().authenticated()
            )
            .formLogin(form -> form.disable())  // Disable default login form
            .logout(logout -> logout.disable()); // Disable default logout

        return http.build();
    }
}

