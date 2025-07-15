package com.swp391.eschoolmed.controller;

import com.swp391.eschoolmed.dto.request.UpdateMedicationStatusRequest;
import com.swp391.eschoolmed.dto.response.MedicationRequestResponse;
import com.swp391.eschoolmed.dto.response.MedicationScheduleForNurse;
import com.swp391.eschoolmed.dto.response.StudentProfileResponse;
import com.swp391.eschoolmed.service.NurseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/nurse")
public class NurseController {

    @Autowired
    private NurseService nurseService;

    //Xác nhận danh sách học sinh theo ID cuộc kiểm tra sức khỏe
    @GetMapping("/confirm-students/{checkupId}")
    public ResponseEntity<List<StudentProfileResponse>> confirmStudents(@PathVariable UUID checkupId) {
        List<StudentProfileResponse> responses = nurseService.confirmStudent(checkupId);
        return ResponseEntity.ok(responses);
    }


    //Cập nhật trạng thái đơn thuốc
    @PutMapping("/update-medication-status")
    public ResponseEntity<Void> updateMedicationStatus(@RequestBody UpdateMedicationStatusRequest request) {
        nurseService.updateMedicationStatus(request);
        return ResponseEntity.ok().build();
    }


    //Lấy danh sách đơn thuốc đang chờ xác nhận
    @GetMapping("/medication-requests/pending")
    public ResponseEntity<List<MedicationRequestResponse>> getPendingMedicationRequests() {
        List<MedicationRequestResponse> responses = nurseService.getPendingMedicationRequests();
        return ResponseEntity.ok(responses);
    }


    //Lấy lịch uống thuốc hôm nay của học sinh
    @GetMapping("/today-schedules/{studentId}")
    public ResponseEntity<List<MedicationScheduleForNurse>> getTodaySchedules(@PathVariable UUID studentId) {
        List<MedicationScheduleForNurse> schedules = nurseService.getTodaySchedulesByStudent(studentId);
        return ResponseEntity.ok(schedules);
    }


    //Đánh dấu lịch đã uống thuốc
    @PutMapping("/mark-schedule-as-taken/{scheduleId}")
    public ResponseEntity<Void> markScheduleAsTaken(@PathVariable UUID scheduleId) {
        nurseService.markScheduleAsTaken(scheduleId);
        return ResponseEntity.ok().build();
    }
}
