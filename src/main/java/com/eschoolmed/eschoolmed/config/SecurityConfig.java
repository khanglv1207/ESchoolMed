package com.eschoolmed.eschoolmed.config;

import com.eschoolmed.eschoolmed.repository.UserRepository;
import com.eschoolmed.eschoolmed.service.impl.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.boot.CommandLineRunner;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationSuccessHandler customSuccessHandler; // ✅ thêm dòng này

    // ✅ Constructor đầy đủ
    public SecurityConfig(CustomUserDetailsService userDetailsService, AuthenticationSuccessHandler customSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.customSuccessHandler = customSuccessHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/register", "/css/**", "/js/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(customSuccessHandler)  // 👈 Thêm dòng này
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")              // 👈 đường dẫn logout (mặc định cũng là /logout)
                        .logoutSuccessUrl("/")             // 👈 về trang Home sau khi logout
                        .invalidateHttpSession(true)       // Xoá session hiện tại
                        .deleteCookies("JSESSIONID")       // Xoá cookie
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService);
        auth.setPasswordEncoder(passwordEncoder());
        return auth;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    @Bean
    public CommandLineRunner encodePasswords(UserRepository userRepo, BCryptPasswordEncoder encoder) {
        return args -> {
            userRepo.findAll().forEach(user -> {
                String rawPassword = user.getPassword();

                // Check tra nếu password chưa mã hóa thì mới mã hóa
                if (!rawPassword.startsWith("$2a$")) {
                    user.setPassword(encoder.encode(rawPassword));
                    userRepo.save(user);
                    System.out.println("Đã mã hóa password cho: " + user.getEmail());
                }
            });
        };
    }
}
