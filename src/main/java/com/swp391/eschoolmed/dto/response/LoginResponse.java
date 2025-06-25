package com.swp391.eschoolmed.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class LoginResponse {
    private UUID id;
    private String email;
    private String token;
    private boolean firstLogin;

}
