package com.swp391.eschoolmed.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "medication_schedule")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicationSchedule {

    @Id
    @Column(name = "schedule_id", nullable = false)
    private UUID scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private MedicationItem item;

    @Column(name = "time_of_day", nullable = false)
    private String timeOfDay;

    @Column(name = "instruction")
    private String instruction;

    @Column(name = "has_taken")
    private Boolean hasTaken = false;

    @Column(name = "taken_time")
    private LocalDateTime takenTime;
}

