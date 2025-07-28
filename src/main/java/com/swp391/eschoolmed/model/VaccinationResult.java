package com.swp391.eschoolmed.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "vaccination_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VaccinationResult {

    @Id
    @GeneratedValue
    @Column(name = "result_id")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "confirmation_id", unique = true)
    private VaccinationConfirmation confirmation;


    @Column(name = "actual_vaccination_date")
    private LocalDateTime actualVaccinationDate;

    private boolean successful;

    private boolean hasReaction;

    private boolean followUpNeeded;

    private boolean needsBooster;

    @Column(name = "reaction_note")
    private String reactionNote;

    @Column(name = "follow_up_end_date")
    private LocalDateTime followUpEndDate;

    private boolean finalized;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
