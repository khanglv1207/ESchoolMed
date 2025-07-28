package com.swp391.eschoolmed.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reminder_schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReminderSchedule {

    @Id
    @GeneratedValue
    @Column(name = "reminder_id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "vaccine_type_id")
    private VaccineType vaccineType;

    @Column(name = "reminder_date")
    private LocalDateTime reminderDate;

    private boolean sent;
}
