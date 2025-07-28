package com.swp391.eschoolmed.dto.request;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class MedicalRequest {
    private UUID studentId;
    private String studentCode;
    private String note;
    private List<MedicationItemRequest> medications;

    @Data
    public static class MedicationItemRequest {
        private String medicationName;
        private String dosage;
        private String note;
        private List<String> schedule;
    }
}
