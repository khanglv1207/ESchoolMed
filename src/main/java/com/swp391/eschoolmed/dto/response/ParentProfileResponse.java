package com.swp391.eschoolmed.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ParentProfileResponse {
    private String userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private String dateOfBirth;
    private List<StudentProfileResponse> children;

}
