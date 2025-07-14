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
        http.cors(Customizer.withDefaults());

        http.authorizeHttpRequests(auth -> auth

                // Public endpoints
                .requestMatchers(HttpMethod.POST, "/api/users/login").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/mail/receive_email").permitAll()

                // Swagger UI
                .requestMatchers(
                        "/api/swagger-ui.html",
                        "/api/swagger-ui/**",
                        "/api/v1/api-docs/**",
                        "/api/swagger-ui/index.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                ).permitAll()

                // Static and public pages
                .requestMatchers("/home", "/login", "/register", "/health-declaration",
                        "/contact", "/vaccination", "/medical-checkup", "/import-students").permitAll()
                .requestMatchers("/static/**", "/images/**", "/css/**", "/js/**").permitAll()

                // Role-based access
                .requestMatchers(HttpMethod.POST, "/api/mail/change-password-first-time").hasAuthority("PARENT")
                .requestMatchers("/create-parent-account", "/admin-dashboard").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/mail/create-parent").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/students/import-student").hasAuthority("ADMIN")

                // Vaccination management
                .requestMatchers(HttpMethod.POST, "/api/vaccinations/**").hasAuthority("MEDICAL_STAFF")
                .requestMatchers(HttpMethod.DELETE, "/api/vaccinations/**").hasAuthority("MEDICAL_STAFF")
                .requestMatchers(HttpMethod.GET, "/api/vaccinations/student/**").hasAnyAuthority("MEDICAL_STAFF", "PARENT")
                .requestMatchers(HttpMethod.GET, "/api/vaccinations/**").authenticated()

                // Medical incident management
                .requestMatchers(HttpMethod.POST, "/api/medical-incidents/**").hasAuthority("MEDICAL_STAFF")
                .requestMatchers(HttpMethod.GET, "/api/medical-incidents/**").hasAuthority("MEDICAL_STAFF")

                // Medical supply management
                .requestMatchers(HttpMethod.POST, "/api/supplies/**").hasAuthority("MEDICAL_STAFF")
                .requestMatchers(HttpMethod.PUT, "/api/supplies/**").hasAuthority("MEDICAL_STAFF")
                .requestMatchers(HttpMethod.DELETE, "/api/supplies/**").hasAuthority("MEDICAL_STAFF")
                .requestMatchers(HttpMethod.GET, "/api/supplies/**").hasAuthority("MEDICAL_STAFF")

                // Default rule: all other endpoints require auth
                .anyRequest().authenticated()
        );

        http.oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                        .decoder(customJwtDecoder)
                        .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
        );

        http.csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix(""); // remove ROLE_ prefix

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return converter;
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
