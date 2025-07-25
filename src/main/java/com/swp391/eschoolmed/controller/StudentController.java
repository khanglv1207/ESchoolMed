package com.swp391.eschoolmed.controller;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.swp391.eschoolmed.model.MedicalCheckupNotification;
import com.swp391.eschoolmed.repository.MedicalCheckupNotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.swp391.eschoolmed.dto.ApiResponse;
import com.swp391.eschoolmed.dto.request.ParentStudentUpdateRequest;
import com.swp391.eschoolmed.dto.request.StudentProfileRequest;
import com.swp391.eschoolmed.dto.response.StudentProfileResponse;
import com.swp391.eschoolmed.repository.StudentRepository;
import com.swp391.eschoolmed.service.StudentService;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private StudentService studentService;

    @Autowired
    private MedicalCheckupNotificationRepository medicalCheckupNotificationRepository;

    @PostMapping(value = "/import-parent-students", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<String> importParentStudentExcel(@RequestParam("file") MultipartFile file) {
        studentService.importParentStudentFromExcel(file);
        return ApiResponse.<String>builder()
                .message("Đã import thành công!")
                .result("OK")
                .build();
    }

    @PutMapping("/update-imported")
    public ApiResponse<Void> updateImportedRecord(@RequestBody ParentStudentUpdateRequest request) {
        studentService.updateImportedParentStudent(request);
        return ApiResponse.<Void>builder()
                .message("Cập nhật thông tin thành công.")
                .result(null)
                .build();
    }


    @PostMapping("/update-profile-student/{studentId}")
    public ApiResponse<Void> updateStudentProfile(@PathVariable UUID studentId,
                                                  @RequestBody StudentProfileRequest request,
                                                  @RequestHeader("Authorization") String authHeader){
        studentService.updateStudentProfile(studentId, request, authHeader);
        return ApiResponse.<Void>builder()
                .message("Cập nhật thông tin học sinh thành công.")
                .result(null)
                .build();
    }






}
