package com.swp391.eschoolmed.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CheckupResultResponse {
    private String studentName;
    private LocalDate checkupDate;
    private String checkupTitle;
    private String resultSummary;
    private Boolean isAbnormal;
    private String suggestion;
}

