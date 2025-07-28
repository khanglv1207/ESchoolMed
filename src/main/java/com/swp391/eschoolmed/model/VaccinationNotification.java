package com.swp391.eschoolmed.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "vaccination_notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VaccinationNotification {

    @Id
    @GeneratedValue
    @Column(name = "notification_id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "vaccine_type_id")
    private VaccineType vaccineType;

    @Column(name = "scheduled_date")
    private LocalDateTime scheduledDate;

    private String location;

    private String note;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
