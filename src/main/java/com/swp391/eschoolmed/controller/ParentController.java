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
import com.swp391.eschoolmed.repository.*;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.swp391.eschoolmed.dto.ApiResponse;
import com.swp391.eschoolmed.dto.request.UpdateParentProfileRequest;
import com.swp391.eschoolmed.dto.response.ParentProfileResponse;
import com.swp391.eschoolmed.service.ParentService;
import com.swp391.eschoolmed.service.UserService;

@RestController
@RequestMapping("/api/parents")
public class ParentController {

    @Autowired
    private ParentService parentService;

    @Autowired
    private MedicationItemRepository medicationItemRepository;
    @Autowired
    private MedicationRequestRepository medicationRequestRepository;
    @Autowired
    private MedicationScheduleRepository medicationScheduleRepository;


    // hiển thị thông tin phụ huynh và học sinh trong phần thông tin
    @GetMapping("/parent-profile")
    public ApiResponse<ParentProfileResponse> getParentProfile(@AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.<ParentProfileResponse>builder()
                .message("Thông tin phụ huynh")
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

    // gửi thuốc
    @PostMapping("/medical-request")
    public ApiResponse<MedicationRequestResponse> sendMedicalRequest(@RequestBody MedicalRequest request,
                                                                     @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        MedicationRequestResponse response = parentService.sendMedicalRequestByUserId(request, userId);
        return ApiResponse.<MedicationRequestResponse>builder()
                .message("Gửi thuốc thành công.")
                .result(response)
                .build();
    }

    // Lấy tất cả đơn thuốc của một học sinh
    @GetMapping("/student/{studentId}")
    public ApiResponse<List<MedicationRequestResponse>> getRequestsByStudent(@PathVariable UUID studentId) {
        List<MedicationRequestResponse> list = parentService.getRequestsByStudentId(studentId);
        return ApiResponse.<List<MedicationRequestResponse>>builder()
                .message("Lấy danh sách đơn thuốc thành công.")
                .result(list)
                .build();
    }






}
