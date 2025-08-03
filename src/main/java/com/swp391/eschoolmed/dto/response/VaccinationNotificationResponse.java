package com.swp391.eschoolmed.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class VaccinationNotificationResponse {
    private String vaccineName;
    private String location;
    private String note;
    private LocalDateTime scheduledDate;
}
