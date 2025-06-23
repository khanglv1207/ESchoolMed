package com.swp391.eschoolmed.controller;

import com.nimbusds.jose.JOSEException;
import com.swp391.eschoolmed.dto.request.ChangePasswordRequest;
import com.swp391.eschoolmed.dto.request.CreateParentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swp391.eschoolmed.dto.ApiResponse;
import com.swp391.eschoolmed.dto.request.LoginRequest;
import com.swp391.eschoolmed.dto.response.LoginResponse;
import com.swp391.eschoolmed.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) throws JOSEException {
        LoginResponse response = userService.login(request.getEmail(), request.getPassword());
        return ApiResponse.<LoginResponse>builder()
                .message("Login thanh cong")
                .result(response)
                .build();

    }



}
