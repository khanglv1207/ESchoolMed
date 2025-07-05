package com.swp391.eschoolmed.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.swp391.eschoolmed.dto.request.MedicalRequest;
import com.swp391.eschoolmed.dto.response.CheckupResultResponse;
import com.swp391.eschoolmed.dto.response.MedicationRequestResponse;
import com.swp391.eschoolmed.model.MedicalCheckupNotification;
import com.swp391.eschoolmed.model.MedicationRequest;
import com.swp391.eschoolmed.model.Parent;
import com.swp391.eschoolmed.model.User;
import com.swp391.eschoolmed.repository.MedicalCheckupNotificationRepository;
import com.swp391.eschoolmed.repository.UserRepository;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
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
    public ApiResponse<ParentProfileResponse> getParentProfile(@AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.<ParentProfileResponse>builder()
                .message("Thông tin của phụ huynh")
                .result(parentService.getParentProfileFromJwt(jwt))
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
    public ApiResponse<List<CheckupResultResponse>> getCheckupResults(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<CheckupResultResponse> results = parentService.getCheckupResult(userDetails.getUsername());
        return ApiResponse.<List<CheckupResultResponse>>builder()
                .message("Danh sách sức khỏe sau khi khám")
                .result(results)
                .build();
    }

    @PostMapping("/medical-request")
    public ApiResponse<MedicationRequest> sendMedicalRequest(@RequestBody MedicalRequest request,
                                                             @AuthenticationPrincipal UserDetails userDetails){
        if (userDetails == null) {
            throw new RuntimeException("Người dùng chưa xác thực.");
        }
        String username = userDetails.getUsername();
        parentService.sendMedicalRequest(request, username);
        return ApiResponse.<MedicationRequest>builder()
                .message("Gửi thuốc thành công.")
                .result(null)
                .build();
    }

    @GetMapping("/medical-view")
    public ApiResponse<List<MedicationRequestResponse>> getMedicationRequests(@AuthenticationPrincipal UserDetails userDetails) {
        List<MedicationRequestResponse> result = parentService.getMedicationRequests(userDetails.getUsername());
        return ApiResponse.<List<MedicationRequestResponse>>builder()
                .message("Lấy danh sách đơn thuốc thành công.")
                .result(result)
                .build();
    }

}
