package com.swp391.eschoolmed.dto.request;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CreateMedicalIncidentRequest {
    private String studentCode;
    private String className;

    private LocalDateTime occurredAt;
    private String incidentType;
    private String incidentDescription;

    private String initialTreatment;
    private String initialResponder;

    private boolean handledByParent;
    private boolean handledByStaff;
    private boolean monitoredBySchool;

    private String currentStatus;
    private String imageUrl;

    private UUID nurseId;
    private UUID parentStudentId;
}

