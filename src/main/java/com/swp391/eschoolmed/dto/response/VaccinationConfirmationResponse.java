package com.swp391.eschoolmed.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
@Builder
public class VaccinationConfirmationResponse {
    private UUID studentId;
    private String studentName;
    private String vaccineName;
    private LocalDate scheduledDate;
    private String status;
    private LocalDateTime confirmedAt;
}
