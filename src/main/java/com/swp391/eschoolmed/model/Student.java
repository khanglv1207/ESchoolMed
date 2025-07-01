package com.swp391.eschoolmed.model;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

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
    private UUID class_id;

    private String studentCode;

    @Column(name = "date_of_birth")
    private LocalDate date_of_birth;

    @Column(name = "gender")
    private String gender;

    @OneToMany(mappedBy = "student")
    private List<MedicalCheckupNotification> checkupNotifications;

    @OneToMany(mappedBy = "student")
    private List<ParentStudent> parentStudents;

}
