package com.swp391.eschoolmed.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class ParentStudentResponse {
    private UUID id;
    private String StudentCode;
    private String studentName;
    private LocalDate studentDob;
    private String gender;
    private String className;

    private String ParentCode;
    private String parentName;
    private String parentEmail;
    private String parentPhone;
    private String relationship;
    private LocalDate parentDob;
    private String parentAddress;
}
