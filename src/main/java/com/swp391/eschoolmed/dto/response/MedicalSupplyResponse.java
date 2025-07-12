package com.swp391.eschoolmed.dto.response;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class MedicalSupplyResponse {
    private UUID id;
    private String name;
    private String type;
    private String unit;
    private int quantity;
    private LocalDate expiryDate;
}
