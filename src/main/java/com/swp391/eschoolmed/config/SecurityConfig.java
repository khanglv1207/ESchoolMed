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


                        // Parent
                        .requestMatchers(HttpMethod.POST, "/api/mail/change-password-first-time").hasAuthority("PARENT")
                        .requestMatchers(HttpMethod.POST, "/api/parents/update-profile-parent").hasAuthority("PARENT")
                        .requestMatchers(HttpMethod.GET, "/api/parents/parent-profile").hasAuthority("PARENT")// hiển thị thông tin hồ sơ
                        .requestMatchers(HttpMethod.GET, "/api/parents/checkup-result").hasAuthority("PARENT")// hiển thị kq sau khi khám
                        .requestMatchers(HttpMethod.POST, "/api/parents/medical-request").hasAuthority("PARENT") // gửi thuốc
                        .requestMatchers(HttpMethod.PUT, "/api/parents/confirm-checkup").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/parents/health-profile").hasAuthority("PARENT")//ph khai báo sức khỏe
                        .requestMatchers(HttpMethod.GET, "/api/parents/health-declaration/latest").hasAuthority("PARENT")// hiển thị tt khai báo sưc khỏe
                        .requestMatchers(HttpMethod.GET, "/api/parents/students").hasAuthority("PARENT")
                        .requestMatchers(HttpMethod.POST, "/api/vaccinations/confirm-vaccination").hasAuthority("PARENT")// ph đồng ý hoặc từ chối
                        .requestMatchers(HttpMethod.GET, "/api/vaccinations/vaccination-result").hasAuthority("PARENT")// hiển thị kq tiêm chủng cho ph

                        // Student/Parent import & update (permitted for all - caution advised)
                        .requestMatchers(HttpMethod.POST, "/api/students/update-profile-student").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/students/update-imported").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/students/import-parent-students").permitAll()

                        // Admin/Nurse-specific endpoints (permitted for now - can be restricted later)
                        .requestMatchers("/create-parent-account", "/admin-dashboard").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/mail/create-parent").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/students/import-student").permitAll()// add danh sách học sinh
                        .requestMatchers(HttpMethod.POST, "/api/mail/send-checkup-notice").permitAll() // gửi mail thông báo ktra
                        .requestMatchers(HttpMethod.GET, "/api/admin/get-all-student-parent").permitAll() // lấy danh sách studetn parent
                        .requestMatchers(HttpMethod.POST, "/api/admin/create-student-parent").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/admin/create-checkup").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/get-all-user").permitAll()// lấy danh sách tài khoản
                        .requestMatchers(HttpMethod.PUT, "/api/users/update-user/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/users/delete-user/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/nurse/confirmed-students").permitAll() //Xác nhận danh sách học sinh theo ID cuộc kiểm tra sức khỏe
                        .requestMatchers(HttpMethod.PUT, "/api/nurse/update-medication-status/").permitAll()//Cập nhật trạng thái đơn thuốc
                        .requestMatchers(HttpMethod.GET, "/api/nurse/medication-requests/pending").permitAll()//Lấy danh sách đơn thuốc đang chờ xác nhận
                        .requestMatchers(HttpMethod.GET, "/api/nurse/today-schedules/").permitAll()//Lấy lịch uống thuốc hôm nay của học sinh
                        .requestMatchers(HttpMethod.PUT, "/api/nurse/mark-schedule-as-taken/").permitAll()//Đánh dấu lịch đã uống thuốc
                        .requestMatchers(HttpMethod.POST, "/api/nurse/health-checkup").permitAll()//lưu thông tin sau khi khám
                        .requestMatchers(HttpMethod.POST, "/api/nurse/get-all-nurse").permitAll()//lấy danh sách y tá
                        .requestMatchers(HttpMethod.POST, "/api/nurse/update-nurse").permitAll()//sửa thông tin y tá
                        .requestMatchers(HttpMethod.POST, "/api/nurse/delete-nurse/").permitAll()// xóa thông tin y tá
                        .requestMatchers(HttpMethod.POST, "/api/medicalIncident/create_medicalIncident").permitAll()//tạo sự cố y tế
                        .requestMatchers(HttpMethod.POST, "/api/medicalIncident/send-incidents").permitAll()// gửi thông báo sự cố đến ph
                        .requestMatchers(HttpMethod.POST, "/api/vaccinations/send-notification").permitAll()// gửi lịch tiêm
                        .requestMatchers(HttpMethod.POST, "/api/vaccinations/send-vaccination-results").permitAll()// gửi kq tiêm
                        .requestMatchers(HttpMethod.GET, "/api/vaccinations/vaccination/pending").permitAll()// lấy danh sách tiêm
                        .requestMatchers(HttpMethod.POST, "/api/vaccinations/vaccination/result").permitAll()// ghi nhận kq tiêm






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