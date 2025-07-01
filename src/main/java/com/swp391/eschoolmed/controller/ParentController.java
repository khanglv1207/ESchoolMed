package com.swp391.eschoolmed.controller;

import java.util.List;
import java.util.UUID;

import com.swp391.eschoolmed.dto.response.CheckupResultResponse;
import com.swp391.eschoolmed.model.MedicalCheckupNotification;
import com.swp391.eschoolmed.model.Parent;
import com.swp391.eschoolmed.model.User;
import com.swp391.eschoolmed.repository.MedicalCheckupNotificationRepository;
import com.swp391.eschoolmed.repository.UserRepository;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swp391.eschoolmed.dto.ApiResponse;
import com.swp391.eschoolmed.dto.request.UpdateParentProfileRequest;
import com.swp391.eschoolmed.dto.response.ParentProfileResponse;
import com.swp391.eschoolmed.repository.ParentRepository;
import com.swp391.eschoolmed.service.ParentService;
import com.swp391.eschoolmed.service.UserService;

@RestController
@RequestMapping("/api/parents")
public class ParentController {

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private ParentService parentService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MedicalCheckupNotificationRepository medicalCheckupNotificationRepository;

    @GetMapping("/parent-profile")
    public ApiResponse<ParentProfileResponse> getParentProfile(@RequestHeader("Authorization") String token) {
        UUID userId = userService.extractUserIdFromToken(token);
        return ApiResponse.<ParentProfileResponse>builder()
                .message("Thông tin của phụ huynh")
                .result(parentService.getParentProfile(userId))
                .build();
    }


    @PostMapping("/update-profile-parent")
    public ApiResponse<Void> updateParentProfile(@RequestBody UpdateParentProfileRequest request){
        parentService.updateParentProfile(request);
        return ApiResponse.<Void>builder()
                .message("Cập nhật thông tin phụ huynh thành công.")
                .result(null)
                .build();
    }

    // hiển thị thông tin sau khi khám
    @GetMapping("/checkup-result")
    public ResponseEntity<List<CheckupResultResponse>> getCheckupResults(
            @AuthenticationPrincipal UserDetails userDetails) {

        String email = userDetails.getUsername(); // lấy email của phụ huynh đang login

        User parentUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Parent parent = parentRepository.findByUser(parentUser)
                .orElseThrow(() -> new RuntimeException("Parent not found"));

        List<MedicalCheckupNotification> notifications =
                medicalCheckupNotificationRepository.findByParent(parent);

        List<CheckupResultResponse> result = notifications.stream()
                .filter(MedicalCheckupNotification::getIsConfirmed)
                .map(notification -> CheckupResultResponse.builder()
                        .studentName(notification.getStudent().getFullName())
                        .checkupDate(notification.getCheckupDate())
                        .checkupTitle(notification.getCheckupTitle())
                        .resultSummary(notification.getResultSummary())
                        .isAbnormal(notification.getIsAbnormal())
                        .suggestion(notification.getSuggestion())
                        .build())
                .toList();

        return ResponseEntity.ok(result);
    }



}
