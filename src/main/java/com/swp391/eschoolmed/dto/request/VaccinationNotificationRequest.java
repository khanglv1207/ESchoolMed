package com.swp391.eschoolmed.dto.request;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class VaccinationNotificationRequest {
    private List<UUID> studentIds;
    private UUID vaccineTypeId;
    private LocalDateTime scheduledDate;
    private String location;
    private String note;
}
