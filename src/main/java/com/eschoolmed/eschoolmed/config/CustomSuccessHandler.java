package com.eschoolmed.eschoolmed.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        String redirectUrl = request.getContextPath(); // đảm bảo đúng URL nếu deploy trong subpath

        for (GrantedAuthority auth : authorities) {
            String role = auth.getAuthority();

            switch (role) {
                case "ROLE_ADMIN":
                case "ROLE_MANAGER":
                    redirectUrl += "/admin/home";
                    break;
                case "ROLE_NURSE":
                    redirectUrl += "/nurse/home";
                    break;
                case "ROLE_USER":
                    redirectUrl += "/user/home";
                    break;
                default:
                    redirectUrl += "/access-denied";
            }

            // Ngay khi tìm được role hợp lệ thì redirect
            response.sendRedirect(redirectUrl);
            return;
        }

        // Nếu không có role nào hợp lệ
        response.sendRedirect(redirectUrl + "/access-denied");
    }
}
