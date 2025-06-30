package com.swp391.eschoolmed.dto.response;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class StudentProfileResponse {
    private UUID studentId;
    private String fullName;
    private UUID class_id;
    private LocalDate date_of_birth;
    private String gender;
}
