package com.swp391.eschoolmed.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CheckupNoticeRequest {
    private String checkupTitle;
    private String content;
    private LocalDate checkupDate;
}
