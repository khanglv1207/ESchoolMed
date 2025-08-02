package com.swp391.eschoolmed.dto.response;

import com.swp391.eschoolmed.model.ClassEntity;
import com.swp391.eschoolmed.model.HealthProfile;
import com.swp391.eschoolmed.model.Parent;
import lombok.Builder;
import lombok.Data;


import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class StudentResponse {
    private UUID studentId;
    private String studentCode;
    private String fullName;
    private Parent parent;
    private UUID class_id;
    private ClassEntity classEntity;
    private LocalDate Date_of_birth;
    private String gender;
    private HealthProfile healthProfile;
}
