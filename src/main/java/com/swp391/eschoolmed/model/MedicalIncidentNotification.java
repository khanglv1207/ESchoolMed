package com.swp391.eschoolmed.model;

import jakarta.persistence.*;
import lombok.Data;
import org.aspectj.lang.annotation.RequiredTypes;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "medical_incident_notification")
public class MedicalIncidentNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "incident_id", referencedColumnName = "id")
    private MedicalIncident incident;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "parent_id")
    private Parent parent;

    private String content;

    private LocalDateTime sentAt;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
