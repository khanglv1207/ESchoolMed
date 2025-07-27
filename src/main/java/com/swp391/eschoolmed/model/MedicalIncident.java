package com.swp391.eschoolmed.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "medical_incident")
@Data
public class MedicalIncident {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "nurse_id")
    private Nurse nurse;

    @ManyToOne
    @JoinColumn(name = "parent_student_id")
    private ParentStudent parentStudent;

    private String studentCode;
    private String className;

    private LocalDateTime occurredAt;
    private String incidentType;
    private String incidentDescription;

    private String initialTreatment;
    private String initialResponder;

    private boolean handledByParent;
    private boolean handledByStaff;
    private boolean monitoredBySchool;

    private String currentStatus;
    private String imageUrl;

    private boolean parentNotified = false;
    private boolean resolved = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

