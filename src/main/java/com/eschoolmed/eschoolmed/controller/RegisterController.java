package com.eschoolmed.eschoolmed.controller;

import com.eschoolmed.eschoolmed.dto.UserRegisterDto;
import com.eschoolmed.eschoolmed.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new UserRegisterDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("user") UserRegisterDto userDto,
            BindingResult bindingResult,
            Model model
    ) {
        if (userService.existsByEmail(userDto.getEmail())) {
            bindingResult.rejectValue("email", null, "Email đã tồn tại");
        }

        if (bindingResult.hasErrors()) {
            return "register";
        }

        userService.register(userDto);
        return "redirect:/login?registerSuccess";
    }
}
