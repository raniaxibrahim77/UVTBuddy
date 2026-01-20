package com.example.studybuddy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain security(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth

                        // public
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()

                        // READ
                        .requestMatchers(HttpMethod.GET, "/api/sessions/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/info/**").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/posts/**").authenticated()

                        // POSTS
                        .requestMatchers(HttpMethod.POST,   "/api/posts").authenticated()
                        .requestMatchers(HttpMethod.PUT,    "/api/posts/*").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/posts/*").authenticated()
                        .requestMatchers(HttpMethod.POST,   "/api/posts/*/images").authenticated()

                        // comments
                        .requestMatchers(HttpMethod.GET,    "/api/posts/*/comments").authenticated()
                        .requestMatchers(HttpMethod.POST,   "/api/posts/*/comments").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/posts/*/comments/*").authenticated()


                        // SESSIONS
                        .requestMatchers(HttpMethod.POST, "/api/sessions").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/sessions/*/join").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/sessions/*/leave").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/api/sessions/*").hasRole("ADMIN")

                        // INFO
                        .requestMatchers(HttpMethod.POST,   "/api/info/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/info/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/info/**").hasRole("ADMIN")

                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )
                .headers(h -> h.frameOptions(f -> f.disable()));

        return http.build();
    }
}
