package com.swp391.eschoolmed.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swp391.eschoolmed.dto.ApiResponse;
import com.swp391.eschoolmed.dto.request.LoginRequest;
import com.swp391.eschoolmed.dto.request.RegisterRequest;
import com.swp391.eschoolmed.dto.response.LoginResponse;
import com.swp391.eschoolmed.dto.response.RegisterResponse;
import com.swp391.eschoolmed.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    ApiResponse<LoginResponse> login(@RequestBody LoginRequest request){
        LoginResponse response = userService.login(request.getEmail(), request.getPassword());
        return ApiResponse.<LoginResponse>builder()
                .message("Login thanh cong")
                .result(response)
                .build();

    }

    @PostMapping("/register")
    ApiResponse<RegisterResponse> register(@RequestBody RegisterRequest request){
        RegisterResponse register = userService.register(request);
            return ApiResponse.<RegisterResponse>builder()
                    .message("Đăng kí tài khoản thành công.")
                    .result(register)
                    .build();
    }

    @Controller
    public class HomeController {
    @RequestMapping(value = { "/", "/{path:[^\\.]*}" }) // tất cả path không chứa dấu chấm
    public String redirect() {
        return "forward:/index.html";
    }
}

}
