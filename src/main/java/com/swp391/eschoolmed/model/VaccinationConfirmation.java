package com.swp391.eschoolmed.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "vaccination_confirmations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VaccinationConfirmation {

    @Id
    @GeneratedValue
    @Column(name = "confirmation_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_id", referencedColumnName = "notification_id")
    private VaccinationNotification notification;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @Enumerated(EnumType.STRING)
    private ConfirmationStatus status;

    @Column(name = "parent_note")
    private String parentNote;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @OneToOne(mappedBy = "confirmation", cascade = CascadeType.ALL)
    private VaccinationResult vaccinationResult;
}

