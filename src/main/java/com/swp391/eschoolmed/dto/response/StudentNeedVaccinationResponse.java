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
    private String studentCode;
    private String studentName;
    private String className;
    private String parentEmail;
    private String vaccineName;
    private LocalDate vaccinationDate;
}