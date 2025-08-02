package com.swp391.eschoolmed.dto.request;

import lombok.Data;

@Data
public class CreateNurseRequest {
    private String fullName;
    private String phone;
    private String specialization;
}
