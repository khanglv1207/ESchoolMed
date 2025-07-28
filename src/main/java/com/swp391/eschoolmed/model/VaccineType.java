package com.swp391.eschoolmed.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "vaccine_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VaccineType {

    @Id
    @GeneratedValue
    @Column(name = "vaccine_type_id")
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(name = "doses_required")
    private int dosesRequired;

    @Column(name = "interval_days")
    private int intervalDays;
}
