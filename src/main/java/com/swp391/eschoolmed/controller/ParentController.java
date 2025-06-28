package com.swp391.eschoolmed.controller;

import com.swp391.eschoolmed.dto.ApiResponse;
import com.swp391.eschoolmed.dto.request.ParentProfileRequest;
import com.swp391.eschoolmed.repository.ParentRepository;
import com.swp391.eschoolmed.service.ParentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/parents")
public class ParentController {

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private ParentService parentService;

    @PostMapping("/update-profile")
    public ApiResponse<Void> updateParentProfile(@RequestBody ParentProfileRequest request){
        parentService.updateParentProfile(request);
        return ApiResponse.<Void>builder()
                .message("Cập nhật thông tin phụ huynh thành công.")
                .result(null)
                .build();
    }

}
