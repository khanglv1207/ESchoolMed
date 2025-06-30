package com.swp391.eschoolmed.controller;

import com.swp391.eschoolmed.dto.ApiResponse;
import com.swp391.eschoolmed.dto.request.ParentProfileRequest;
import com.swp391.eschoolmed.dto.request.StudentProfileRequest;
import com.swp391.eschoolmed.dto.response.ParentProfileResponse;
import com.swp391.eschoolmed.dto.response.StudentProfileResponse;
import com.swp391.eschoolmed.repository.StudentRepository;
import com.swp391.eschoolmed.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private StudentService studentService;

    @PostMapping(value = "/import-student", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> importExcel(@RequestParam("file") MultipartFile file) {
        try {
            studentService.importExcel(file);
            return ResponseEntity.ok("Thêm học sinh vào danh sách thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Thêm học sinh thất bại" + e.getMessage());
        }
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

    @GetMapping("/profile-student/{studentId}")
    ApiResponse<StudentProfileResponse> getStudentProfile(@PathVariable UUID studentId) {
        return ApiResponse.<StudentProfileResponse>builder()
                .message("Thông tin của học sinh")
                .result(studentService.getStudentProfile(studentId))
                .build();
    }
}
