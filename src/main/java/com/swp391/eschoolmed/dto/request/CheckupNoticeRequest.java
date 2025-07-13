package com.swp391.eschoolmed.dto.request;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CheckupNoticeRequest {
    private String checkupTitle;
    private String content;
    private LocalDate checkupDate;
}
