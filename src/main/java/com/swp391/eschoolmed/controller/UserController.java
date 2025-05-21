package com.swp391.eschoolmed.controller;

import com.swp391.eschoolmed.model.User;
import com.swp391.eschoolmed.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // Trang đăng ký
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // Xử lý đăng ký
    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            model.addAttribute("error", "Email đã được đăng ký.");
            return "register";
        }

        // Gán vai trò mặc định là 'parent'
        user.setRole("parent");

        userRepository.save(user);
        model.addAttribute("message", "Tạo tài khoản thành công. Vui lòng đăng nhập!");
        return "login";
    }

}
