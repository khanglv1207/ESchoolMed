package com.swp391.eschoolmed.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class CheckedStudentResponse {
    private UUID studentId;
    private String studentName;
    private String className;
    private LocalDate checkupDate;
    private Double heightCm;
    private Double weightKg;
    private String nurseName;
}
