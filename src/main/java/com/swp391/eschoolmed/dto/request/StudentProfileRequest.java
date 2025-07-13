package com.swp391.eschoolmed.dto.request;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class StudentProfileRequest {
    private UUID studentId;
    private String fullName;
    private UUID class_id;
    private LocalDate date_of_birth;
    private String gender;
}
