package com.swp391.eschoolmed.controller;

import com.swp391.eschoolmed.dto.request.CheckupResultRequest;
import com.swp391.eschoolmed.dto.response.StudentProfileResponse;
import com.swp391.eschoolmed.model.MedicalCheckupNotification;
import com.swp391.eschoolmed.model.Student;
import com.swp391.eschoolmed.repository.MedicalCheckupNotificationRepository;
import com.swp391.eschoolmed.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/nurses")
public class NurseController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private MedicalCheckupNotificationRepository medicalCheckupNotificationRepository;

    @GetMapping("/check-confirmStudent")
    public ResponseEntity<List<StudentProfileResponse>> getConfirmedStudents(@RequestParam UUID checkupId) {
        List<MedicalCheckupNotification> notifications = medicalCheckupNotificationRepository
                .findByCheckupTitleAndIsConfirmedTrue(String.valueOf(checkupId)); // hoặc checkupTitle tuỳ cách bạn lưu

        List<Student> students = notifications.stream()
                .map(MedicalCheckupNotification::getStudent)
                .collect(Collectors.toList());

        List<StudentProfileResponse> response = students.stream()
                .map(studentService::toStudentProfileResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/checkup-result/{notificationId}")
    public ResponseEntity<?> updateCheckupResult(
            @PathVariable UUID notificationId,
            @RequestBody CheckupResultRequest request) {

        Optional<MedicalCheckupNotification> optional =
                medicalCheckupNotificationRepository.findById(notificationId);

        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Không tìm thấy thông báo khám.");
        }

        MedicalCheckupNotification notification = optional.get();
        notification.setResultSummary(request.getResultSummary());
        notification.setIsAbnormal(request.getIsAbnormal());
        notification.setSuggestion(request.getSuggestion());

        medicalCheckupNotificationRepository.save(notification);

        return ResponseEntity.ok("Đã lưu kết quả khám thành công.");
    }


}
