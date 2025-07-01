package com.swp391.eschoolmed.controller;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swp391.eschoolmed.dto.ApiResponse;
import com.swp391.eschoolmed.dto.request.UpdateParentProfileRequest;
import com.swp391.eschoolmed.dto.response.ParentProfileResponse;
import com.swp391.eschoolmed.repository.ParentRepository;
import com.swp391.eschoolmed.service.ParentService;
import com.swp391.eschoolmed.service.UserService;

@RestController
@RequestMapping("/api/parents")
public class ParentController {

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private ParentService parentService;

    @Autowired
    private UserService userService;

    @GetMapping("/parent-profile")
    public ApiResponse<ParentProfileResponse> getParentProfile(@RequestHeader("Authorization") String token) {
        UUID userId = userService.extractUserIdFromToken(token);
        return ApiResponse.<ParentProfileResponse>builder()
                .message("Thông tin của phụ huynh")
                .result(parentService.getParentProfile(userId))
                .build();
    }


    @PostMapping("/update-profile-parent")
    public ApiResponse<Void> updateParentProfile(@RequestBody UpdateParentProfileRequest request){
        parentService.updateParentProfile(request);
        return ApiResponse.<Void>builder()
                .message("Cập nhật thông tin phụ huynh thành công.")
                .result(null)
                .build();
    }

}
