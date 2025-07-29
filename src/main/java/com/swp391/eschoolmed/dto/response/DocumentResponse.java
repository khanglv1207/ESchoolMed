package com.swp391.eschoolmed.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DocumentResponse {
    private Long documentId;
    private Long userId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
}
