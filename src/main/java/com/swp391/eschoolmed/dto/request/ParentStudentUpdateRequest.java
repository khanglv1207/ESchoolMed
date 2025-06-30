package com.swp391.eschoolmed.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class ParentStudentUpdateRequest {
    private UUID id;

    private String studentName;
    private String className;
    private LocalDate studentDob;
    private String gender;

    private String parentName;
    private String parentPhone;
    private LocalDate parentDob;
    private String parentAddress;

    private String relationship;

    private UUID parentId;
    private UUID studentId;
}
