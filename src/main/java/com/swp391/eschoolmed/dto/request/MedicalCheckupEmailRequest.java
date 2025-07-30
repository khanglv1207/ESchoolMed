package com.swp391.eschoolmed.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MedicalCheckupEmailRequest {
    private String checkupTitle;
    private LocalDate checkupDate;
    private String content;
}
