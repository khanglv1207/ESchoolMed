package com.swp391.eschoolmed.dto.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SendVaccinationNoticeRequest {
    private UUID vaccineTypeId;
    private LocalDateTime scheduledDate;
    private String location;
    private String note;
}
