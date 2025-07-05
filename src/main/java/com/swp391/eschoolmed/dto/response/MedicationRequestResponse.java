package com.swp391.eschoolmed.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class MedicationRequestResponse {
    private UUID requestId;
    private String medicationName;
    private String dosage;
    private String frequency;
    private String note;
    private LocalDateTime requestDate;
    private String status;

    private String parentName;
    private String studentName;
}
