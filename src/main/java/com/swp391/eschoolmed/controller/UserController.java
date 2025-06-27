package com.swp391.eschoolmed.controller;

import com.nimbusds.jose.JOSEException;
import com.swp391.eschoolmed.dto.request.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swp391.eschoolmed.dto.ApiResponse;
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
                .message("Login thành công")
                .result(response)
                .build();
    }

    @PostMapping("/request-password-reset")
    ApiResponse<Void> requestReset(@RequestBody RequestResetRequest request) {
        userService.requestPasswordRequest(request.getEmail());
        return ApiResponse.<Void>builder()
                .message("OTP đã được gửi qua mail")
                .result(null)
                .build();
    }

    @PostMapping("/verify-otp")
    ApiResponse<Void> verifyOtp(@RequestBody VerifyOtpRequest request) {
        userService.verifyOtp(request.getEmail(), request.getOtpCode());
        return ApiResponse.<Void>builder()
                .message("Otp hợp lệ")
                .result(null)
                .build();
    }

    @PostMapping("/reset-password")
    ApiResponse<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request.getEmail(), request.getNewPassword());
        return ApiResponse.<Void>builder()
                .message("Mật khẩu đã được thay đổi")
                .result(null)
                .build();
    }

}
