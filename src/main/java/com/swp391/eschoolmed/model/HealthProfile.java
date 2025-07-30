package com.swp391.eschoolmed.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "health_profiles")
@NoArgsConstructor
@AllArgsConstructor
public class HealthProfile {

    @Id
    @GeneratedValue
    @Column(name = "profile_id")
    private UUID profileId;

    @OneToOne
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    private String allergies;
    private String chronicDiseases;
    private String medicalHistory;
    private String eyesight;
    private String hearing;
    private String vaccinationRecord;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "last_updated_at")
    private LocalDateTime updatedAt;
}
