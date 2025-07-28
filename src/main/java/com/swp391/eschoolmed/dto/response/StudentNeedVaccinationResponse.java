package com.swp391.eschoolmed.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class StudentNeedVaccinationResponse {
    private UUID confirmationId;
    private UUID studentId;
    private String fullName;
    private String className;
    private String vaccineName;
    private LocalDate vaccinationDate;
}