package com.swp391.eschoolmed.dto.request;

import java.util.UUID;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private UUID userId;
    private String newPassword;
}
