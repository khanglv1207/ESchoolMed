package com.swp391.eschoolmed.dto.response;

import com.swp391.eschoolmed.model.MedicationSchedule;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class MedicationItemResponse {
    private UUID itemId;
    private String medicationName;
    private String dosage;
    private String note;
    private List<MedicationSchedule> schedules;
}
