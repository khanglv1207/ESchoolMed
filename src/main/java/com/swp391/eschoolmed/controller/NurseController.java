package com.swp391.eschoolmed.controller;

import com.swp391.eschoolmed.dto.ApiResponse;
import com.swp391.eschoolmed.dto.request.CheckupResultRequest;
import com.swp391.eschoolmed.dto.request.UpdateMedicationStatusRequest;
import com.swp391.eschoolmed.dto.response.MedicationRequestResponse;
import com.swp391.eschoolmed.dto.response.MedicationScheduleForNurse;
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


    // xác nhận danh sách học sinh khám sức khỏe
    @GetMapping("/check-confirmStudent")
    public ApiResponse<List<StudentProfileResponse>> getConfirmedStudents(@RequestParam UUID checkupId) {
        List<StudentProfileResponse> students = nurseService.confirmStudent(checkupId);
        return ApiResponse.<List<StudentProfileResponse>>builder()
                .message("Danh sách học sinh đã xác nhận khám sức khỏe")
                .result(students)
                .build();
    }

    //Y tá lấy danh sách lịch uống thuốc hôm nay theo học sinh
    @GetMapping("/students/{studentId}/schedules")
    public ApiResponse<List<MedicationScheduleForNurse>> getTodaySchedules(@PathVariable UUID studentId) {
        List<MedicationScheduleForNurse> list = nurseService.getTodaySchedulesByStudent(studentId);
        return ApiResponse.<List<MedicationScheduleForNurse>>builder()
                .message("Lịch uống thuốc hôm nay của học sinh")
                .result(list)
                .build();
    }

    //Y tá đánh dấu đã uống
    @PatchMapping("/schedules/{scheduleId}/mark-taken")
    public ApiResponse<String> markScheduleAsTaken(@PathVariable UUID scheduleId) {
        nurseService.markScheduleAsTaken(scheduleId);
        return ApiResponse.<String>builder()
                .message("Đã đánh dấu lịch uống là đã uống.")
                .result("OK")
                .build();
    }

}
