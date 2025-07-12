package com.swp391.eschoolmed.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class MedicalIncident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Student student;

    private String incidentType; // "Sốt", "Té ngã", "Tai nạn", "Dịch bệnh"...

    private String description;

    private LocalDateTime occurredAt;

    @ManyToOne
    private User staff; // Nhân viên y tế ghi nhận

    private boolean parentNotified = false;

    private boolean resolved = false;
}

