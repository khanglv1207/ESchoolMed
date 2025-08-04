package com.swp391.eschoolmed.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class MedicalCheckupResultEmailRequest {
    private UUID checkupId;
    private LocalDate date;
}
