package com.swp391.eschoolmed.controller;

import com.swp391.eschoolmed.dto.ApiResponse;
import com.swp391.eschoolmed.dto.request.*;
import com.swp391.eschoolmed.dto.response.*;
import com.swp391.eschoolmed.model.VaccinationNotification;
import com.swp391.eschoolmed.service.VaccinationService;

import com.swp391.eschoolmed.service.mail.MailService;
import io.swagger.v3.core.util.OpenAPI30To31;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vaccinations")
public class VaccinationController {

    @Autowired
    private VaccinationService vaccinationService;

    @Autowired
    private MailService mailService;

    // thêm vaccin
    @PostMapping("/create-vaccine-type")
    public ApiResponse<?> createVaccineType(@RequestBody CreateVaccineTypeRequest request) {
        vaccinationService.createVaccineType(request);
        return ApiResponse.builder()
                .message("Tạo loại vaccine thành công.")
                .code(1000)
                .build();
    }

    //lấy danh sách vaccin
    @GetMapping("/vaccine-types")
    public ApiResponse<List<GetAllVaccineTypesResponse>> getAllVaccineTypes() {
        List<GetAllVaccineTypesResponse> types = vaccinationService.getAllVaccineTypes();
        return ApiResponse.<List<GetAllVaccineTypesResponse>>builder()
                .code(1000)
                .message("Lấy danh sách loại vaccine thành công.")
                .result(types)
                .build();
    }

    // lấy ds hoọc sinh tiêm loại vaccin:
    @GetMapping("/students-to-vaccinate")
    public ApiResponse<List<StudentNeedVaccinationResponse>> getStudentsToVaccinate(@RequestParam String vaccineName,
                                                                                    @AuthenticationPrincipal Jwt jwt) {
        String role = jwt.getClaimAsString("scope");
        if (!"ADMIN".equalsIgnoreCase(role) && !"NURSE".equalsIgnoreCase(role)) {
            throw new AccessDeniedException("Bạn không có quyền thực hiện thao tác này.");
        }
        List<StudentNeedVaccinationResponse> students = vaccinationService
                .findEligibleStudentsForNotification(vaccineName);
        return ApiResponse.<List<StudentNeedVaccinationResponse>>builder()
                .code(1000)
                .message("Lấy danh sách học sinh đủ điều kiện gửi thông báo thành công.")
                .result(students)
                .build();
    }


    // Gửi thông báo tiêm chủng đến phụ huynh các học sinh cần tiêm
    @PostMapping("/send-vaccination-notices")
    public ApiResponse<Void> sendVaccinationNotices(
            @RequestBody VaccinationNotificationRequest request,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String role = jwt.getClaimAsString("scope");
        if (!"ADMIN".equalsIgnoreCase(role) && !"NURSE".equalsIgnoreCase(role)) {
            throw new AccessDeniedException("Bạn không có quyền gửi thông báo tiêm chủng.");
        }

        mailService.sendVaccinationNotices(request);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Đã gửi thông báo tiêm chủng thành công.")
                .build();
    }

    //hiển thị thông báo tiêm chủng
    @GetMapping("/notifications")
    public ApiResponse<List<VaccinationNotificationResponse>> getVaccinationNotifications(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        List<VaccinationNotificationResponse> notifications = vaccinationService.getVaccinationNotifications(userId);
        return ApiResponse.<List<VaccinationNotificationResponse>>builder()
                .code(1000)
                .message("Lấy danh sách thông báo tiêm chủng thành công.")
                .result(notifications)
                .build();
    }

    // ph đồng ý hoặc từ chối
    @PostMapping("/confirm-vaccination")
    public ApiResponse<Void> confirmVaccination(@RequestBody VaccinationConfirmationRequest request,
                                                @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        vaccinationService.confirmVaccination(userId, request);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Xác nhận tiêm chủng thành công.")
                .build();
    }

    // hiển thị danh sách xác nhận tiêm chủng của phụ huynh
    @GetMapping("/confirmation-status")
    public ApiResponse<List<VaccinationConfirmationResponse>> getVaccinationConfirmations(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        List<VaccinationConfirmationResponse> responses = vaccinationService.getVaccinationConfirmations(userId);
        return ApiResponse.<List<VaccinationConfirmationResponse>>builder()
                .code(1000)
                .message("Lấy danh sách xác nhận tiêm chủng thành công.")
                .result(responses)
                .build();
    }


    //y tá lấy danh sách cần tiêm
    @GetMapping("/students-need-vaccination")
    public ApiResponse<List<StudentNeedVaccinationResponse>> getPendingVaccinations() {
        List<StudentNeedVaccinationResponse> result = vaccinationService.getStudentsNeedVaccination();
        return ApiResponse.<List<StudentNeedVaccinationResponse>>builder()
                .code(1000)
                .message("Lấy danh sách học sinh cần tiêm thành công.")
                .result(result)
                .build();
    }

    // ghi nhận kết quả tiêm
    @PostMapping("/vaccination/result")
    public ApiResponse<Void> createVaccinationResult(@RequestBody VaccinationResultRequest request) {
        vaccinationService.createVaccinationResult(request);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Lưu kết quả tiêm thành công.")
                .build();
    }

    // gửi kq tiêm chủng
    @PostMapping("/send-vaccination-results")
    public ApiResponse<Void> sendVaccinationResults() {
        mailService.sendVaccinationResultsToParents();
        return ApiResponse.<Void>builder()
                .message("Đã gửi kết quả tiêm chủng.")
                .code(1000)
                .build();
    }

    // hiển thị kq tiêm chủng cho ph
    @GetMapping("/vaccination-result")
    public ApiResponse<List<VaccinationResultResponse>> getVaccinationResult(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());

        List<VaccinationResultResponse> responses = vaccinationService.getVaccinationResultsForParent(userId);

        return ApiResponse.<List<VaccinationResultResponse>>builder()
                .code(1000)
                .message("Lấy kết quả tiêm chủng thành công.")
                .result(responses)
                .build();
    }






}
