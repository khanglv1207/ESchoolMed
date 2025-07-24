package com.swp391.eschoolmed.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class ConfirmCheckupRequest {
    private UUID notificationId;
    private boolean confirmed;
}
