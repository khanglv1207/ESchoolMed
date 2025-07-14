package com.swp391.eschoolmed.controller;

import com.swp391.eschoolmed.dto.request.*;
import com.swp391.eschoolmed.dto.response.GetAllUserResponse;
import com.swp391.eschoolmed.model.User;
import org.apache.commons.collections4.Get;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.nimbusds.jose.JOSEException;
import com.swp391.eschoolmed.dto.ApiResponse;
import com.swp391.eschoolmed.dto.response.LoginResponse;
import com.swp391.eschoolmed.service.UserService;

import java.util.List;
import java.util.UUID;

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

    @GetMapping("/get-all-user")
    ApiResponse<List<GetAllUserResponse>> getAllUser() {
        List<GetAllUserResponse> responses = userService.getAllUser();
        return ApiResponse.<List<GetAllUserResponse>>builder()
                .message("Danh sách user.")
                .result(responses)
                .build();
    }

    @PutMapping("/update-user/{id}")
    ApiResponse<GetAllUserResponse> updateUser(@PathVariable UUID id, @RequestBody UpdateUserRequest request){
        GetAllUserResponse update = userService.updateUser(id,request);
        return ApiResponse.<GetAllUserResponse>builder()
                .message("Cập nhật user thành công.")
                .result(update)
                .build();
    }

    @DeleteMapping("/delete-user/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ApiResponse.<Void>builder()
                .message("Xóa user thành công.")
                .code(1000)
                .build();
    }


}
