package com.swp391.eschoolmed.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class UpdateStudentParentRequest {
    private String studentCode;
    private String studentName;
    private String className;
    private LocalDate studentDob;
    private String gender;

    private String parentCode;
    private String parentName;
    private String parentEmail;
    private String parentPhone;
    private LocalDate parentDob;
    private String parentAddress;

    private String relationship;
    private String status;
}
