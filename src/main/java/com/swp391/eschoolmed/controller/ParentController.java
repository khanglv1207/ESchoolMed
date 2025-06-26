package com.swp391.eschoolmed.controller;

import com.swp391.eschoolmed.repository.ParentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/parents")
public class ParentController {

    @Autowired
    private ParentRepository parentRepository;

//    @PostMapping("/profile")
//    public ApiResponse<Void> updateParentProfile(@RequestBody ParentProfileRequest request,
//                                                 @AuthenticationPrincipal UserPrincipal userPrincipal){
//        UUID userId = userPrincipal.getUserId();
//        parentRepository.updateParentProfile(userId, request);
//
//        return ApiResponse.<Void>builder()
//                .message("Cập nhật thông tin phụ huynh thành công.")
//                .result(null)
//                .build();
//    }

}
