package com.swp391.eschoolmed.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateMedicationStatusRequest {
    private UUID requestId;
    private String status;
    private String note;
}
