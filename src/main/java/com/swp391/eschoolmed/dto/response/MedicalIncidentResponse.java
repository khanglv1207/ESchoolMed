package com.swp391.eschoolmed.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MedicalIncidentResponse {

    private String studentName;
    private String incidentType;
    private String description;
    private LocalDateTime occurredAt;
    private String staffName;
}
