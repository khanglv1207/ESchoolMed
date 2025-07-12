package com.swp391.eschoolmed.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "students")
@Data
public class Student {

    @Id
    @Column(name = "student_id")
    private UUID studentId;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "class_id")
    private UUID classId;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gender")
    private String gender;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private User parent;
}
