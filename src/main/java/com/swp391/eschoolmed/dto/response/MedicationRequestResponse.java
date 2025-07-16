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
    private LocalDateTime requestDate;
    private String parentName;
    private String studentName;
    private String status;
    private String note;
}
