package com.swp391.eschoolmed.dto.request;

import lombok.Data;

@Data
public class CreateStudentParentRequest {
    private String studentName;
    private String studentDob;
    private String gender;
    private String className;

    private String parentName;
    private String parentEmail;
    private String parentPhone;
    private String parentDob;
    private String parentAddress;

    private String relationship;
}
