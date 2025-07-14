package com.swp391.eschoolmed.dto.request;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String fullName;
    private String email;
    private String passwordHash;
    private String role;
}
