package com.swp391.eschoolmed.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.swp391.eschoolmed.dto.ApiResponse;
import com.swp391.eschoolmed.dto.request.ChangePasswordRequest;
import com.swp391.eschoolmed.dto.request.CheckupNoticeRequest;
import com.swp391.eschoolmed.dto.request.CreateParentRequest;
import com.swp391.eschoolmed.service.mail.MailService;

@RestController
@RequestMapping("/api/mail")
public class MailController {

    @Autowired
    private MailService mailService;

    @GetMapping("/receive_email")
    void receiveEmail(@RequestParam String receiverEmail,
                      @RequestParam String fullName,
                      @RequestParam int age,
                      @RequestParam String tempPassword) {
        mailService.sendNewPassword(receiverEmail, fullName, age, tempPassword);
    }

    // đổi mk lần đầu
    @PostMapping("/change-password-first-time")
    public ApiResponse<Void> changePasswordFirstTime(@RequestBody ChangePasswordRequest request) {
        mailService.changePasswordFirstTime(request.getUserId(), request.getNewPassword());
        return ApiResponse.<Void>builder()
                .message("Thay đổi mật khẩu thành công")
                .build();
    }

    //tạo tk cho ph
    @PostMapping("/create-parent")
    ApiResponse<String> createParent(@RequestBody CreateParentRequest request) {
        mailService.createParentAccount(request.getEmail(), request.getFullName());
        return ApiResponse.<String>builder()
                .message("Tạo tài khoản phụ huynh thành công. Mật khẩu được gửi qua email.")
                .result("OK")
                .build();
    }

    // gửi thông báo ktra y tế
    @PutMapping("/send-checkup-notices")
    public ApiResponse<String> sendCheckupNotices() {
        mailService.sendMedicalCheckupNotices(); // không truyền gì cả
        return ApiResponse.<String>builder()
                .message("Đã gửi thông báo kiểm tra y tế đến phụ huynh thành công.")
                .result("OK")
                .build();
    }


}
