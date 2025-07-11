package com.swp391.eschoolmed.config;

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

import lombok.RequiredArgsConstructor;

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
                        // cho phép tat cả
                        .requestMatchers(HttpMethod.POST, "/api/users/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/mail/receive_email").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/request-password-reset").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/verify-otp").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users/reset-password").permitAll()

                        // user
                        .requestMatchers(HttpMethod.POST, "/api/mail/change-password-first-time").hasAuthority("PARENT")
                        .requestMatchers(HttpMethod.POST, "/api/parents/update-profile-parent").hasAuthority("PARENT")
                        .requestMatchers(HttpMethod.GET, "/api/parents/parent-profile").hasAuthority("PARENT")
                        .requestMatchers(HttpMethod.POST, "/api/students/update-profile-student").hasAuthority("PARENT")
                        .requestMatchers(HttpMethod.PUT,"/api/students/update-imported").hasAuthority("PARENT")
                        .requestMatchers(HttpMethod.POST,"/api/students/import-parent-students").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/students/parent-checkup-confirm").hasAuthority("PARENT")
                        .requestMatchers(HttpMethod.GET, "/api/parents/checkup-result").hasAuthority("PARENT")
                        .requestMatchers(HttpMethod.POST,"/api/parents/medical-request").hasAuthority("PARENT")
                        .requestMatchers(HttpMethod.GET,"/api/parents/student/**").hasAuthority("PARENT")

                        // truy cập swagger
                        .requestMatchers(HttpMethod.GET, "/api/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/swagger-ui/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/v1/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/swagger-ui/index.html").permitAll()

                        // admin
                        .requestMatchers(HttpMethod.POST, "/api/mail/create-parent").permitAll()//tạo tk cho ph
                        .requestMatchers(HttpMethod.POST, "/api/students/import-student").hasAuthority("ADMIN")// add danh sách hs
                        .requestMatchers(HttpMethod.POST, "/api/mail/send-checkup-notice").hasAuthority("ADMIN")// gửi thông báo ktra y tế
                        .requestMatchers(HttpMethod.GET, "/api/nurses/check-confirmStudent").hasAuthority("ADMIN")// xác nhận danh sách học sinh khám sức khỏe
                        .requestMatchers(HttpMethod.GET, "/api/admin/get-all-student-parent").permitAll()// hiển thị danh sách hs ph
                        .requestMatchers(HttpMethod.POST, "/api/admin/create-student-parent").hasAuthority("ADMIN")// tạo hs ph
                        .requestMatchers(HttpMethod.GET, "/api/nurses/students/**").hasAuthority("ADMIN")//Y tá lấy danh sách lịch uống thuốc hôm nay theo học sinh
                        .requestMatchers(HttpMethod.PATCH, "/api/nurses/schedules/**").hasAuthority("ADMIN")//Y tá đánh dấu đã uống
                        .requestMatchers(HttpMethod.DELETE, "/api/admin/delete-student-parent/**").hasAuthority("ADMIN")// xóa hs ph theo id
                        .requestMatchers(HttpMethod.PUT, "/api/admin/update-student-parent").hasAuthority("ADMIN")// update hs ph
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
        corsConfiguration.addAllowedOrigin("http://localhost:3002");
        corsConfiguration.addAllowedOrigin("http://localhost:8080");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setAllowCredentials(true); // Nếu dùng cookie

        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(urlBasedCorsConfigurationSource);
    }




}