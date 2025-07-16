package com.swp391.eschoolmed.dto.request;

import java.time.LocalDate;
import java.util.UUID;

import lombok.Data;

@Data
public class ParentProfileRequest {
    private UUID userid;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private LocalDate dateOfBirth;
}
