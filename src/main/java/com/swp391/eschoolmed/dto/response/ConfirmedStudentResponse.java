package com.swp391.eschoolmed.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ConfirmedStudentResponse {
    private UUID notificationId;
    private String studentName;
    private String className;
    private String gender;
    private Boolean isConfirmed;
}
