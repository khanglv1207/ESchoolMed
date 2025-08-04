package com.swp391.eschoolmed.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "health_checkups")
@Data
public class HealthCheckup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checkup_id")
    private MedicalCheckupNotification notification;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "nurse_id")
    private Nurse nurse;

    private LocalDate checkupDate;
    private Double heightCm;
    private Double weightKg;
    private String visionLeft;
    private String visionRight;
    private String notes;
    private boolean resultSent = false;
}
