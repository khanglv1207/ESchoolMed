package com.swp391.eschoolmed.dto.response;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class GetAllNurseResponse {
    private UUID nurseId;
    private String fullName;
    private String email;
    private String phone;
    private String specialization;
}
