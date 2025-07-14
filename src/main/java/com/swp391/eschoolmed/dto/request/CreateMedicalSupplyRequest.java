package com.swp391.eschoolmed.dto.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateMedicalSupplyRequest {
    private String name;
    private String type;
    private String unit;
    private int quantity;
    private LocalDate expiryDate;
}
