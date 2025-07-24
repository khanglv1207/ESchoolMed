package com.swp391.eschoolmed.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class MedicalCheckupCreateRequest {
    private String checkupTitle;
    private LocalDate checkupDate;
    private String content;
    private List<UUID> studentIds;
}
