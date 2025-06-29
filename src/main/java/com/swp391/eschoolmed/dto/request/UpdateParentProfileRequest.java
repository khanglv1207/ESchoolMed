package com.swp391.eschoolmed.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateParentProfileRequest {
    private UUID userid;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String dateOfBirth;

}
