package com.swp391.eschoolmed.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateHealthCheckupRequest {
    private UUID checkupId;
    private UUID studentId;
    private UUID nurseId;
    private LocalDate checkupDate;
    private Double heightCm;
    private Double weightKg;
    private String visionLeft;
    private String visionRight;
    private String notes;
}
