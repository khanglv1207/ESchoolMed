package com.swp391.eschoolmed.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class GetAllVaccineTypesResponse {
    private UUID id;
    private String name;
    private String description;
    private int dosesRequired;
    private int intervalDays;
}
