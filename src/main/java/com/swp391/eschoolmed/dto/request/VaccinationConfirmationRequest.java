package com.swp391.eschoolmed.dto.request;

import com.swp391.eschoolmed.model.ConfirmationStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class VaccinationConfirmationRequest {
    private UUID confirmationId;
    private ConfirmationStatus status;
    private String parentNote;
}
