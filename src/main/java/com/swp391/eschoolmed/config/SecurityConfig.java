package com.swp391.eschoolmed.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomJwtDecoder customJwtDecoder;



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        http.cors(Customizer.withDefaults());
        http
                .authorizeHttpRequests(auth -> auth
                // cho phép tat cả
                .requestMatchers(HttpMethod.POST, "/api/users/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/mail/receive_email").permitAll()

                // user

                .requestMatchers(HttpMethod.POST, "/api/mail/change-password-first-time")
                .hasAuthority("PARENT")

                // truy cập swagger
                .requestMatchers(HttpMethod.GET, "/api/swagger-ui.html").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/swagger-ui/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/v1/api-docs/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/swagger-ui/index.html").permitAll()

                // truy cập các page
                .requestMatchers("/home", "/login", "/register", "/health-declaration",
                        "/contact", "/vaccination", "/medical-checkup", "/import-students")
                .permitAll()
                .requestMatchers("/static/**", "/images/**", "/css/**", "/js/**").permitAll()

                // admin
                .requestMatchers("/create-parent-account", "/admin-dashboard").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/mail/create-parent").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/students/import-student").hasAuthority("ADMIN")

                .anyRequest().authenticated() // tat ca cac request toi API khac deu can JWT
        );

        http.oauth2ResourceServer(
                oauth2 -> oauth2.jwt(
                                jwtConfigurer -> jwtConfigurer
                                        .decoder(customJwtDecoder)
                                        .jwtAuthenticationConverter(converter()))
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint()));


        http.csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    JwtAuthenticationConverter converter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("http://localhost:3000");
        corsConfiguration.addAllowedOrigin("http://localhost:3002"); // Thêm dòng này!
        corsConfiguration.addAllowedOrigin("http://localhost:8080");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setAllowCredentials(true); // Nếu dùng cookie

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(urlBasedCorsConfigurationSource);
    }



}