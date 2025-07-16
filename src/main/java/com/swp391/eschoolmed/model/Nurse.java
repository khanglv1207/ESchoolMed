package com.swp391.eschoolmed.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "nurse")
@Data
public class Nurse {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "nurse_id")
    private UUID nurseId;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    private String specialization;
}
