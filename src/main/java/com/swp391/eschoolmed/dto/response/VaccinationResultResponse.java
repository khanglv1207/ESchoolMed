package com.swp391.eschoolmed.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class VaccinationResultResponse {
    private UUID confirmationId;
    private String studentName;
    private String className;
    private String vaccineName;
    private LocalDateTime vaccinationDate;
    private boolean hasReaction;
    private String reactionNote;
    private boolean needsBooster;
    private boolean finalized;
}
