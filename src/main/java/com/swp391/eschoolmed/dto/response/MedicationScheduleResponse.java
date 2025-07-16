package com.swp391.eschoolmed.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class MedicationScheduleResponse {
    private UUID scheduleId;
    private String medicationName;
    private String timeOfDay;
    private String instruction;
    private boolean hasTaken;
    private LocalDateTime takenTime;
}
