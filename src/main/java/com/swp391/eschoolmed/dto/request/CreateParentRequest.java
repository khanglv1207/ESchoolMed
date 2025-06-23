package com.swp391.eschoolmed.dto.request;

import lombok.Data;

@Data
public class CreateParentRequest {
    private String email;
    private String fullName;
    private int age;
}