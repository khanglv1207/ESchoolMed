package com.swp391.eschoolmed.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateStudentParentRequest {
    private String studentName;
    private LocalDate studentDob;
    private String gender;
    private String className;

    private String parentName;
    private String parentEmail;
    private String parentPhone;
    private LocalDate parentDob;
    private String parentAddress;

    private String relationship;
}
