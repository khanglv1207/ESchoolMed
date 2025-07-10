package com.swp391.eschoolmed.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "medication_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicationItem {

    @Id
    @Column(name = "item_id", nullable = false)
    private UUID itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id", nullable = false)
    private MedicationRequest request;

    @Column(name = "medication_name", nullable = false)
    private String medicationName;

    @Column(name = "dosage")
    private String dosage;

    @Column(name = "note")
    private String note;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicationSchedule> schedules = new ArrayList<>();
}

