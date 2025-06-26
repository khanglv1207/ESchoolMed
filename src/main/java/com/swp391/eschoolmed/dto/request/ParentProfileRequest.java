package com.swp391.eschoolmed.dto.request;

import lombok.Data;

@Data
public class ParentProfileRequest {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String dateOfBirth;
}
