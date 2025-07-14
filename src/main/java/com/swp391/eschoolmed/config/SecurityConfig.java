package com.swp391.eschoolmed.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomJwtDecoder customJwtDecoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers(HttpMethod.POST, "/api/users/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/mail/receive_email").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/request-password-reset").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/verify-otp").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/reset-password").permitAll()

                        // Swagger access
                        .requestMatchers(HttpMethod.GET, "/api/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/swagger-ui/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/swagger-ui/index.html").permitAll()

                        // Static resources
                        .requestMatchers("/home", "/login", "/register", "/health-declaration",
                                "/contact", "/vaccination", "/medical-checkup", "/import-students").permitAll()
                        .requestMatchers("/static/**", "/images/**", "/css/**", "/js/**").permitAll()

                        // Parent-specific endpoints
                        .requestMatchers(HttpMethod.POST, "/api/mail/change-password-first-time").hasAuthority("PARENT")
                        .requestMatchers(HttpMethod.POST, "/api/parents/update-profile-parent").hasAuthority("PARENT")
                        .requestMatchers(HttpMethod.GET, "/api/parents/parent-profile").hasAuthority("PARENT")
                        .requestMatchers(HttpMethod.GET, "/api/students/parent-checkup-confirm").hasAuthority("PARENT")
                        .requestMatchers(HttpMethod.GET, "/api/parents/checkup-result").hasAuthority("PARENT")
                        .requestMatchers(HttpMethod.POST, "/api/parents/medical-request").hasAuthority("PARENT")
                        .requestMatchers(HttpMethod.GET, "/api/parents/student/**").hasAuthority("PARENT")

                        // Student/Parent import & update (permitted for all - caution advised)
                        .requestMatchers(HttpMethod.POST, "/api/students/update-profile-student").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/students/update-imported").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/students/import-parent-students").permitAll()

                        // Admin/Nurse-specific endpoints (permitted for now - can be restricted later)
                        .requestMatchers("/create-parent-account", "/admin-dashboard").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/mail/create-parent").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/students/import-student").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/mail/send-checkup-notice").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/nurses/check-confirmStudent").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/admin/get-all-student-parent").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/admin/create-student-parent").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/nurses/students/**").permitAll()
                        .requestMatchers(HttpMethod.PATCH, "/api/nurses/schedules/**").permitAll()

                        // Everything else requires authentication
                        .anyRequest().authenticated()
                );

        http.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwtConfigurer ->
                        jwtConfigurer.decoder(customJwtDecoder)
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
        corsConfiguration.addAllowedOrigin("http://localhost:3002");
        corsConfiguration.addAllowedOrigin("http://localhost:8080");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(source);
    }
}