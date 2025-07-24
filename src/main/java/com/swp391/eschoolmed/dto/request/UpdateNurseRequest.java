package com.swp391.eschoolmed.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateNurseRequest {
    private UUID nurseId;
    private String fullName;
    private String email;
    private String phone;
    private String specialization;
}

