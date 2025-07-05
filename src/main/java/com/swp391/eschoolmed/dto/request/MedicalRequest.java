package com.swp391.eschoolmed.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class MedicalRequest {
    private UUID studentId;
    private String medicationName;
    private String dosage;
    private String frequency;
    private String note;
}
