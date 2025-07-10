package com.swp391.eschoolmed.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "medication_requests")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicationRequest {
    @Id
    @Column(name = "request_id")
    private UUID requestId;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Parent parent;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MedicationItem> items;

    @Column(name = "note")
    private String note;

    @Column(name = "request_date")
    private LocalDateTime requestDate;

    @Column(name = "status")
    private String status;

    @Column(name = "processed_time")
    private LocalDateTime processedTime;
}
