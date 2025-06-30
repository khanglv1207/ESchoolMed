package com.swp391.eschoolmed.controller;

import com.swp391.eschoolmed.dto.ApiResponse;
import com.swp391.eschoolmed.dto.request.ParentProfileRequest;
import com.swp391.eschoolmed.dto.request.ParentStudentUpdateRequest;
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

    @GetMapping("/profile-student/{studentId}")
    ApiResponse<StudentProfileResponse> getStudentProfile(@PathVariable UUID studentId) {
        return ApiResponse.<StudentProfileResponse>builder()
                .message("Thông tin của học sinh")
                .result(studentService.getStudentProfile(studentId))
                .build();
    }
}
