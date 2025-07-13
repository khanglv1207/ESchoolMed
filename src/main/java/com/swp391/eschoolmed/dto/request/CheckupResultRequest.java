package com.swp391.eschoolmed.dto.request;

import lombok.Data;

@Data
public class CheckupResultRequest {
    private String resultSummary;
    private Boolean isAbnormal;
    private String suggestion;
}
