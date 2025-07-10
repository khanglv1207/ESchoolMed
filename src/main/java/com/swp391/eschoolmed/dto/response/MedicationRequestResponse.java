package com.swp391.eschoolmed.dto.response;

import com.swp391.eschoolmed.model.MedicationItem;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class MedicationRequestResponse {
    private UUID requestId;
    private String note;
    private String status;
    private LocalDateTime requestDate;
    private String studentName;
    private String parentName;
    private List<MedicationItem> medications;
}
