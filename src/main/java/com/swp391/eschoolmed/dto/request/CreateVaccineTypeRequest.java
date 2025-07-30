package com.swp391.eschoolmed.dto.request;

import lombok.Data;

@Data
public class CreateVaccineTypeRequest {
    private String name;
    private String description;
    private int dosesRequired;
    private int intervalDays;
}
