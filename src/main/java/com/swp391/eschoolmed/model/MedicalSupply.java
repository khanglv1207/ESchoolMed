package com.swp391.eschoolmed.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "medical_supplies")
@Data
public class MedicalSupply {

    @Id
    @GeneratedValue
    @Column(name = "supply_id")
    private UUID id;

    private String name;
    private String type;
    private String unit;
    private int quantity;
    private LocalDate expiryDate;
}
