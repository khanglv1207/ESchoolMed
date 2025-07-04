package com.swp391.eschoolmed.controller;

import com.swp391.eschoolmed.dto.ApiResponse;
import com.swp391.eschoolmed.dto.request.CreateStudentParentRequest;
import com.swp391.eschoolmed.dto.response.ParentStudentResponse;
import com.swp391.eschoolmed.model.ParentStudent;
import com.swp391.eschoolmed.model.Student;
import com.swp391.eschoolmed.service.AdminService;
import com.swp391.eschoolmed.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private AdminService adminService;

    @GetMapping("/get-all-student-parent")
    public ApiResponse<List<ParentStudentResponse>> getAllParentStudent(){
        List<ParentStudentResponse> responses = adminService.getAllParentStudent();
        return ApiResponse.<List<ParentStudentResponse>> builder()
                .message("Hiển thị danh sách.")
                .result(responses)
                .build();
    }

    @PostMapping("/create-student-parent")
    public ApiResponse<?> createStudentParent(@RequestBody CreateStudentParentRequest request) {
        adminService.createStudentAndParent(request);
        return ApiResponse.builder()
                .message("Tạo thành công.")
                .result(request)
                .code(1000)
                .build();
    }


}
