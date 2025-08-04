package com.swp391.eschoolmed.controller;

import com.swp391.eschoolmed.dto.ApiResponse;
import com.swp391.eschoolmed.dto.request.*;
import com.swp391.eschoolmed.dto.response.*;
import com.swp391.eschoolmed.repository.StudentRepository;
import com.swp391.eschoolmed.repository.VaccineTypeRepository;
import com.swp391.eschoolmed.service.NurseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/nurse")
public class NurseController {

    @Autowired
    private NurseService nurseService;

    @Autowired
    private VaccineTypeRepository vaccineTypeRepository;

    @Autowired
    private StudentRepository  studentRepository;

    @PostMapping("/create-nurse")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ApiResponse<String> createNurseFromUser(@RequestParam String email) {
        nurseService.createNurseFromUser(email);
        return ApiResponse.<String>builder()
                .message("Tạo y tá thành công từ tài khoản người dùng.")
                .result("OK")
                .build();
    }


    // lấy danh sách y tá
   @GetMapping("/get-all-nurse")
   public ApiResponse<List<GetAllNurseResponse>> getAllNurses(){
       List<GetAllNurseResponse> responses = nurseService.getAllNurses();
       return ApiResponse.<List<GetAllNurseResponse>> builder()
               .message("Lấy danh sách y tá thành công")
               .result(responses)
               .build();
    }

    //update
    @PutMapping("/update-nurse")
    public ApiResponse<String> updateNurse(@RequestBody UpdateNurseRequest request) {
        nurseService.updateNurse(request);
        return ApiResponse.<String>builder()
                .message("Cập nhật y tá thành công")
                .result("OK")
                .build();
    }

    // xóa
    @DeleteMapping("/delete-nurse/{id}")
    public ApiResponse<String> deleteNurse(@PathVariable("id") UUID nurseId) {
        nurseService.deleteNurse(nurseId);
        return ApiResponse.<String>builder()
                .message("Xóa y tá thành công")
                .result("OK")
                .build();
    }

    //hiển thị thông báo kiểm tra y tế
    @GetMapping("/medical-checkup-notices")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('NURSE')")
    public ApiResponse<List<MedicalCheckupNoticeResponse>> getMedicalCheckupNotices() {
        List<MedicalCheckupNoticeResponse> notices = nurseService.getAllMedicalCheckupNoticesForAdminOrNurse();
        return ApiResponse.<List<MedicalCheckupNoticeResponse>>builder()
                .code(1000)
                .message("Lấy danh sách thông báo kiểm tra y tế thành công.")
                .result(notices)
                .build();
    }


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
    public ApiResponse<String> createHealthCheckup(@RequestBody CreateHealthCheckupRequest request,
                                                   @AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        nurseService.createHealthCheckup(request, userId);
        return ApiResponse.<String>builder()
                .message("Đã lưu thông tin khám sức khỏe thành công.")
                .result("OK")
                .build();
    }


    //hiển thị danh sách sau khám
    @GetMapping("/checked-students")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('NURSE')")
    public ApiResponse<List<CheckedStudentResponse>> getAllCheckedStudents() {
        List<CheckedStudentResponse> result = nurseService.getAllCheckedStudents();
        return ApiResponse.<List<CheckedStudentResponse>>builder()
                .code(1000)
                .message("Lấy danh sách học sinh đã khám sức khỏe thành công.")
                .result(result)
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
    @GetMapping("/today-schedules")
    public ApiResponse<List<MedicationScheduleForNurse>> getTodaySchedulesForAllStudents() {
        List<MedicationScheduleForNurse> schedules = nurseService.getTodaySchedulesForAllStudents();
        return ApiResponse.<List<MedicationScheduleForNurse>>builder()
                .message("Lấy lịch uống thuốc hôm nay cho tất cả học sinh thành công.")
                .result(schedules)
                .code(1000)
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
