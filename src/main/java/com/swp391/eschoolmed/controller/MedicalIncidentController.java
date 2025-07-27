package com.swp391.eschoolmed.controller;

import com.swp391.eschoolmed.dto.ApiResponse;
import com.swp391.eschoolmed.dto.request.CreateMedicalIncidentRequest;
import com.swp391.eschoolmed.service.MedicalIncidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/medicalIncident")
public class MedicalIncidentController {

    @Autowired
    private MedicalIncidentService medicalIncidentService;

    //tạo sự cố y tế
    @PostMapping("/create_medicalIncident")
    public ApiResponse<String> createIncident(@RequestBody CreateMedicalIncidentRequest request) {
        medicalIncidentService.createIncident(request);
        return ApiResponse.<String>builder()
                .message("Ghi nhận sự cố y tế thành công.")
                .result("OK")
                .build();
    }

    // thông báo sự cố đến phụ huynh
    @PostMapping("/send-incidents")
    public ApiResponse<String> sendIncidentNotifications() {
        medicalIncidentService.sendIncidentNotifications();
        return ApiResponse.<String>builder()
                .message("Đã xử lý gửi thông báo sự cố y tế")
                .result("OK")
                .build();
    }

}
