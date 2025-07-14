package com.swp391.eschoolmed.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class VaccinationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Học sinh được tiêm
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    // Vaccine được sử dụng
    @ManyToOne
    @JoinColumn(name = "vaccine_id", nullable = false)
    private Vaccine vaccine;

    private LocalDate vaccinationDate;

    private String dose; // Mũi 1, Mũi 2, Nhắc lại

    private String note;
}
