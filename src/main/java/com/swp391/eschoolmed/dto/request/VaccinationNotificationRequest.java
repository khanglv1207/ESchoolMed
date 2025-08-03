package com.swp391.eschoolmed.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class VaccinationNotificationRequest {
    private String vaccineName;
    private LocalDate scheduledDate;
    private String location;
    private String note;
}
