package com.swp391.eschoolmed.dto.request;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;
@Data
public class CreateVaccinationRecordRequest {
    private UUID studentId;
    private Long vaccineId;
    private LocalDate vaccinationDate;
    private String dose;
    private String note;
}
