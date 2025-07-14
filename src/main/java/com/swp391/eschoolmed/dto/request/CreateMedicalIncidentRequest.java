package com.swp391.eschoolmed.dto.request;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CreateMedicalIncidentRequest {
    private UUID studentId;
    private String incidentType;
    private String description;
    private LocalDateTime occurredAt;
    private UUID staffId;
}

