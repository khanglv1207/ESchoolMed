package com.swp391.eschoolmed.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class ParentProfileRequest {
    private UUID parentId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String dateOfBirth;
}
