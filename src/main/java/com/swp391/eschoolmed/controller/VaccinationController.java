package com.swp391.eschoolmed.controller;

import com.swp391.eschoolmed.dto.ApiResponse;
import com.swp391.eschoolmed.dto.request.CreateVaccinationRecordRequest;
import com.swp391.eschoolmed.dto.request.SendVaccinationNoticeRequest;
import com.swp391.eschoolmed.dto.request.VaccinationConfirmationRequest;
import com.swp391.eschoolmed.dto.request.VaccinationResultRequest;
import com.swp391.eschoolmed.dto.response.StudentNeedVaccinationResponse;
import com.swp391.eschoolmed.dto.response.VaccinationRecordResponse;
import com.swp391.eschoolmed.dto.response.VaccinationResultResponse;
import com.swp391.eschoolmed.service.VaccinationService;

import com.swp391.eschoolmed.service.mail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vaccinations")
public class VaccinationController {

    @Autowired
    private VaccinationService vaccinationService;

    @Autowired
    private MailService mailService;

    // gửi lịch tiêm
    @PostMapping("/send-notification")
    public ApiResponse<Void> sendVaccinationNotices(@RequestBody SendVaccinationNoticeRequest request) {
        mailService.sendVaccinationNotices(request);
        return ApiResponse.<Void>builder()
                .message("Đã gửi thông báo tiêm chủng đến phụ huynh")
                .build();
    }

    // ph đồng ý hoặc từ chối
    @PostMapping("/confirm-vaccination")
    public ApiResponse<Void> confirmVaccination(@RequestBody VaccinationConfirmationRequest request,
                                                @AuthenticationPrincipal Jwt jwt
    ) {
        UUID parentId = UUID.fromString(jwt.getSubject());

        vaccinationService.confirmVaccination(parentId, request);

        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Xác nhận tiêm chủng thành công.")
                .build();
    }

    //y tá lấy danh sách cần tiêm
    @GetMapping("/vaccination/pending")
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
    public ApiResponse<Void> recordResult(@RequestBody VaccinationResultRequest request) {
        vaccinationService.recordVaccinationResult(request);
        return ApiResponse.<Void>builder()
                .code(1000)
                .message("Ghi nhận kết quả tiêm thành công.")
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

    // hiển thị danh sách tiêm chủng cho ph
    @GetMapping("/vaccination-result")
    public ApiResponse<List<VaccinationResultResponse>> getVaccinationResults(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        List<VaccinationResultResponse> responses = vaccinationService.getVaccinationResultsByParent(userId);

        return ApiResponse.<List<VaccinationResultResponse>>builder()
                .code(1000)
                .message("Lấy kết quả tiêm chủng thành công.")
                .result(responses)
                .build();
    }




}
