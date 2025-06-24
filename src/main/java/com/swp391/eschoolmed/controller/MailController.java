package com.swp391.eschoolmed.controller;

import com.swp391.eschoolmed.dto.ApiResponse;
import com.swp391.eschoolmed.dto.request.ChangePasswordRequest;
import com.swp391.eschoolmed.dto.request.CreateParentRequest;
import com.swp391.eschoolmed.service.mail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/change-password-first-time")
    ApiResponse<Void> changePasswordFirstTime(@RequestBody ChangePasswordRequest request) {
        mailService.changePasswordFirstTime(request.getUserId(), request.getNewPassword());
        return ApiResponse.<Void>builder()
                .message("Thay đổi mật khẩu thành công")
                .build();
    }

    @PostMapping("/create-parent")
    ApiResponse<String> createParent(@RequestBody CreateParentRequest request) {
        mailService.createParentAccount(request.getEmail(), request.getFullName(), request.getAge());
        return ApiResponse.<String>builder()
                .message("Tạo tài khoản phụ huynh thành công. Mật khẩu được gửi qua email.")
                .result("OK")
                .build();
    }

}
