package com.swp391.eschoolmed.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "parents_students")
@Data
public class ParentStudent {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "parent_id", referencedColumnName = "parent_id")
    private Parent parent;

    @ManyToOne
    @JoinColumn(name = "student_id", referencedColumnName = "student_id")
    private Student student;

    private String relationship;

    @Column(name = "student_code")
    private String studentCode;

    @Column(name = "student_name")
    private String studentName;

    @Column(name = "class_name")
    private String className;

    @Column(name = "student_dob")
    private LocalDate studentDob;

    @Column(name = "gender")
    private String gender;

    @Column(name = "parent_code")
    private String parentCode;

    @Column(name = "parent_name")
    private String parentName;

    @Column(name = "parent_email")
    private String parentEmail;

    @Column(name = "parent_phone")
    private String parentPhone;

    @Column(name = "parent_dob")
    private LocalDate parentDob;

    @Column(name = "parent_address")
    private String parentAddress;

    @Column(name = "status")
    private String status;
}
