package com.swp391.eschoolmed.dto.response;

import lombok.Data;

@Data
public class ParentProfileResponse {
    private String userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String dateOfBirth;


}
