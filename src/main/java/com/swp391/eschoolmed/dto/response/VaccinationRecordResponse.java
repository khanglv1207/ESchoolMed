package com.swp391.eschoolmed.dto.response;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;
@Data
public class VaccinationRecordResponse {
    private Long id;
    private UUID studentId;
    private String studentName;
    private Long vaccineId;
    private String vaccineName;
    private LocalDate vaccinationDate;
    private String dose;
    private String note;
}
