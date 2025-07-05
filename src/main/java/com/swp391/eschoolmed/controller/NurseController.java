package com.swp391.eschoolmed.controller;

import com.swp391.eschoolmed.dto.ApiResponse;
import com.swp391.eschoolmed.dto.request.CheckupResultRequest;
import com.swp391.eschoolmed.dto.request.UpdateMedicationStatusRequest;
import com.swp391.eschoolmed.dto.response.MedicationRequestResponse;
import com.swp391.eschoolmed.dto.response.StudentProfileResponse;
import com.swp391.eschoolmed.model.MedicalCheckupNotification;
import com.swp391.eschoolmed.model.Student;
import com.swp391.eschoolmed.repository.MedicalCheckupNotificationRepository;
import com.swp391.eschoolmed.service.NurseService;
import com.swp391.eschoolmed.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/nurses")
public class NurseController {
    @Autowired
    private NurseService nurseService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private MedicalCheckupNotificationRepository medicalCheckupNotificationRepository;

    @GetMapping("/check-confirmStudent")
    public ApiResponse<List<StudentProfileResponse>> getConfirmedStudents(@RequestParam UUID checkupId) {
        List<StudentProfileResponse> students = nurseService.confirmStudent(checkupId);
        return ApiResponse.<List<StudentProfileResponse>>builder()
                .message("Danh sách học sinh đã xác nhận khám sức khỏe")
                .result(students)
                .build();
    }

    @PutMapping("/medication-requests/update")
    public ApiResponse<Void> updateMedicationRequestStatus(@RequestBody UpdateMedicationStatusRequest request) {
        nurseService.updateMedicationStatus(request);
        return ApiResponse.<Void>builder()
                .message("Cập nhật trạng thái đơn thuốc thành công.")
                .result(null)
                .build();
    }

    // danh sách xử lý đơn thuốc
    @GetMapping("/medication-requests/pending")
    public ApiResponse<List<MedicationRequestResponse>> getPendingMedicationRequests() {
        List<MedicationRequestResponse> responses = nurseService.getPendingMedicationRequests();
        return ApiResponse.<List<MedicationRequestResponse>>builder()
                .message("Lấy danh sách đơn thuốc đang chờ xử lý thành công.")
                .result(responses)
                .build();
    }

    // xử lý đơn thuốc
    @PutMapping("/medication-requests/{requestId}/process")
    public ApiResponse<Void> processMedicationRequest(
            @PathVariable UUID requestId,
            @RequestParam("status") String status // APPROVED hoặc REJECTED
    ) {
        nurseService.processMedicationRequest(requestId, status);
        return ApiResponse.<Void>builder()
                .message("Xử lý đơn thuốc thành công.")
                .result(null)
                .build();
    }

}
