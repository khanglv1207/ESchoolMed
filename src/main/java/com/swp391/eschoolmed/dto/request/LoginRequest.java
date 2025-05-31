package com.swp391.eschoolmed.dto.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String password;
}
