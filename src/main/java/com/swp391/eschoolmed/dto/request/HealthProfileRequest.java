package com.swp391.eschoolmed.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class HealthProfileRequest {
    private String allergies;
    private String chronicDiseases;
    private String medicalHistory;
    private String eyesight;
    private String hearing;
    private String vaccinationRecord;
}
