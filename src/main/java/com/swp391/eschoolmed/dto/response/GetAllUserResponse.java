package com.swp391.eschoolmed.dto.response;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.UUID;

@Data
public class GetAllUserResponse {
    private UUID id;
    private String fullName;
    private String email;
    private String passwordHash;
    private String role = "parent";
    private boolean mustChangePassword = false;
}
