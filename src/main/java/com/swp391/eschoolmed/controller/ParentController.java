package com.swp391.eschoolmed.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.swp391.eschoolmed.dto.request.ConfirmCheckupRequest;
import com.swp391.eschoolmed.dto.request.HealthProfileRequest;
import com.swp391.eschoolmed.dto.request.MedicalRequest;
import com.swp391.eschoolmed.dto.response.*;
import com.swp391.eschoolmed.model.MedicalCheckupNotification;
import com.swp391.eschoolmed.model.MedicationRequest;
import com.swp391.eschoolmed.model.Parent;
import com.swp391.eschoolmed.model.User;
import com.swp391.eschoolmed.repository.*;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.swp391.eschoolmed.dto.ApiResponse;
import com.swp391.eschoolmed.dto.request.UpdateParentProfileRequest;
import com.swp391.eschoolmed.service.ParentService;
import com.swp391.eschoolmed.service.UserService;

@RestController
@RequestMapping("/api/parents")
public class ParentController {

    @Autowired
    private ParentService parentService;

    // hiển thị thông tin phụ huynh và học sinh trong phần thông tin
    @GetMapping("/parent-profile")
    public ApiResponse<ParentProfileResponse> getParentProfile(@AuthenticationPrincipal Jwt jwt) {
        return ApiResponse.<ParentProfileResponse>builder()
                .message("Thông tin phụ huynh")
                .result(parentService.getParentProfileFromJwt(jwt))
                .build();
    }

    // lấy danh sách học sinh của phụ huynh đang đăng nhập
    @GetMapping("/students")
    public ApiResponse<List<ParentStudentResponse>> getLinkedStudents(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        List<ParentStudentResponse> responses = parentService.getStudentsOfLoggedInParent(userId);
        return ApiResponse.<List<ParentStudentResponse>>builder()
                .message("Lấy danh sách học sinh thành công.")
                .result(responses)
                .build();
    }

    //cập nhật thông tin hồ sơ
    @PostMapping("/update-profile-parent")
    public ApiResponse<Void> updateParentProfile(@RequestBody UpdateParentProfileRequest request){
        parentService.updateParentProfile(request);
        return ApiResponse.<Void>builder()
                .message("Cập nhật thông tin phụ huynh thành công.")
                .result(null)
                .build();
    }

    //ph xác nhận tham gia
    @PutMapping("/confirm-checkup")
    public ApiResponse<Void> confirmMedicalCheckup(@RequestBody ConfirmCheckupRequest request,
                                                   @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        parentService.confirmCheckup(userId, request);
        return ApiResponse.<Void>builder()
                .message("Phản hồi của phụ huynh đã được ghi nhận.")
                .code(1000)
                .build();
    }

    // hiển thị thông tin sau khi khám
    @GetMapping("/checkup-result")
    public ApiResponse<List<CheckupResultResponse>> getCheckupResults(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        List<CheckupResultResponse> result = parentService.getCheckupResult(userId);
        return ApiResponse.<List<CheckupResultResponse>>builder()
                .code(1000)
                .message("Lấy kết quả khám sức khỏe thành công.")
                .result(result)
                .build();
    }



    // gửi thuốc
    @PostMapping("/medical-request")
    @PreAuthorize("hasAuthority('PARENT')")
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
    @GetMapping("/schedules")
    public ApiResponse<List<MedicationScheduleResponse>> getSchedulesForParent(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        List<MedicationScheduleResponse> schedules = parentService.getSchedulesForLoggedInParent(userId);
        return ApiResponse.<List<MedicationScheduleResponse>>builder()
                .message("Lấy lịch uống thuốc thành công.")
                .result(schedules)
                .build();
    }

    //ph khai báo sức khỏe
    @PostMapping("/health-profile")
    public ResponseEntity<ApiResponse<String>> createOrUpdateHealthProfile(@RequestBody HealthProfileRequest request,
                                                                           @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        parentService.createOrUpdateHealthProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .message("Khai báo sức khỏe thành công")
                .result("OK")
                .build());
    }

    // hiển thị tt khai báo sưc khỏe
    @GetMapping("/health-declaration/latest")
    public ApiResponse<HealthProfileResponse> getLatestHealthDeclaration(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        HealthProfileResponse response = parentService.getLatestHealthProfile(userId);
        return ApiResponse.<HealthProfileResponse>builder()
                .message("Lấy khai báo sức khỏe mới nhất thành công.")
                .result(response)
                .build();
    }






}
