package com.swp391.eschoolmed.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class HealthProfileResponse {
    private String studentName;
    private String allergies;
    private String chronicDiseases;
    private String medicalHistory;
    private String eyesight;
    private String hearing;
    private String vaccinationRecord;

}
