package com.swp391.eschoolmed.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class MedicalCheckupNoticeResponse {
    private UUID id;
    private String checkupTitle;
    private LocalDate checkupDate;
    private String studentName;
    private String className;
    private Boolean isConfirmed;
    private LocalDateTime sentAt;
    private LocalDateTime confirmedAt;
}
