package com.swp391.eschoolmed.controller;

import com.swp391.eschoolmed.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute User user, Model model) {
        if ("admin".equals(user.getUsername()) && "1234".equals(user.getPassword())) {
            model.addAttribute("message", "Đăng nhập thành công!");
            return "welcome";
        } else {
            model.addAttribute("error", "Sai tài khoản hoặc mật khẩu.");
            return "login";
        }
    }
}
