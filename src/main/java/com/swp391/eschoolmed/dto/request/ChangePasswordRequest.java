package com.swp391.eschoolmed.dto.request;


import lombok.Data;

import java.util.UUID;

@Data
public class ChangePasswordRequest {
    private UUID userId;
    private String newPassword;
}
