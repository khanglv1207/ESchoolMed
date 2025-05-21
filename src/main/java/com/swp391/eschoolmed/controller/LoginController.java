package com.swp391.eschoolmed.controller;

import com.swp391.eschoolmed.model.User;
import com.swp391.eschoolmed.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute User input, Model model) {
        var foundUser = userRepository.findByEmailAndPasswordHash(input.getEmail(), input.getPasswordHash());
        if (foundUser.isPresent()) {
            model.addAttribute("message", "Đăng nhập thành công! Xin chào " + foundUser.get().getFullName());
            return "welcome";
        } else {
            model.addAttribute("error", "Sai email hoặc mật khẩu.");
            return "login";
        }

    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Xóa session hiện tại
        return "redirect:/login";
    }
}
