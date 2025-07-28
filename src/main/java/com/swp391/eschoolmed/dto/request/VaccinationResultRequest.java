package com.swp391.eschoolmed.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class VaccinationResultRequest {
    private UUID confirmationId;
    private LocalDateTime vaccinationDate;
    private String notes;
    private boolean hasReaction; // phản ứng sau tiêm
    private boolean followUpNeeded; // cần theo dõi sau tiêm
    private boolean needsBooster; // cần nhắc tiêm lại
}

