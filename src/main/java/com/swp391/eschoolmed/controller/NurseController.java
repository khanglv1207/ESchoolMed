package com.swp391.eschoolmed.controller;

import com.swp391.eschoolmed.dto.ApiResponse;
import com.swp391.eschoolmed.dto.request.CreateHealthCheckupRequest;
import com.swp391.eschoolmed.dto.request.UpdateMedicationStatusRequest;
import com.swp391.eschoolmed.dto.response.ConfirmedStudentResponse;
import com.swp391.eschoolmed.dto.response.MedicationRequestResponse;
import com.swp391.eschoolmed.dto.response.MedicationScheduleForNurse;
import com.swp391.eschoolmed.dto.response.StudentProfileResponse;
import com.swp391.eschoolmed.model.MedicalCheckupNotification;
import com.swp391.eschoolmed.service.NurseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/nurse")
public class NurseController {

    @Autowired
    private NurseService nurseService;

    //Xác nhận danh sách học sinh theo ID cuộc kiểm tra sức khỏe
    @GetMapping("/confirmed-students")
    public ApiResponse<List<ConfirmedStudentResponse>> getConfirmedStudents() {
        List<ConfirmedStudentResponse> result = nurseService.getConfirmedStudents();
        return ApiResponse.<List<ConfirmedStudentResponse>>builder()
                .code(0)
                .message("Danh sách học sinh đã xác nhận")
                .result(result)
                .build();
    }

    //lưu thông tin sau khi khám
    @PostMapping("/health-checkup")
    public ApiResponse<String> createHealthCheckup(@RequestBody CreateHealthCheckupRequest request) {
        nurseService.createHealthCheckup(request);
        return ApiResponse.<String>builder()
                .message("Đã lưu thông tin khám sức khỏe thành công.")
                .result("OK")
                .build();
    }

    //Cập nhật trạng thái đơn thuốc
    @PutMapping("/update-medication-status")
    public ApiResponse<String> updateMedicationStatus(@RequestBody UpdateMedicationStatusRequest request) {
        nurseService.updateMedicationStatus(request);
        return ApiResponse.<String>builder()
                .message("Cập nhật trạng thái đơn thuốc thành công.")
                .result("OK")
                .build();
    }


    //Lấy danh sách đơn thuốc đang chờ xác nhận
    @GetMapping("/medication-requests/pending")
    public ApiResponse<List<MedicationRequestResponse>> getPendingMedicationRequests() {
        List<MedicationRequestResponse> requests = nurseService.getPendingMedicationRequests();
        return ApiResponse.<List<MedicationRequestResponse>>builder()
                .message("Lấy danh sách đơn thuốc đang chờ xác nhận thành công.")
                .result(requests)
                .build();
    }



    //Lấy lịch uống thuốc hôm nay của học sinh
    @GetMapping("/today-schedules/{studentId}")
    public ApiResponse<List<MedicationScheduleForNurse>> getTodaySchedules(@PathVariable UUID studentId) {
        List<MedicationScheduleForNurse> schedules = nurseService.getTodaySchedulesByStudent(studentId);
        return ApiResponse.<List<MedicationScheduleForNurse>>builder()
                .message("Lấy lịch uống thuốc hôm nay thành công.")
                .result(schedules)
                .build();
    }


    //Đánh dấu lịch đã uống thuốc
    @PutMapping("/mark-schedule-as-taken/{scheduleId}")
    public ApiResponse<String> markScheduleAsTaken(@PathVariable UUID scheduleId) {
        nurseService.markScheduleAsTaken(scheduleId);
        return ApiResponse.<String>builder()
                .message("Đánh dấu đã uống thuốc thành công.")
                .result("OK")
                .build();
    }
}
