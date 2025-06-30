package com.swp391.eschoolmed.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
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
    private UUID class_id;

    @Column(name = "date_of_birth")
    private LocalDate date_of_birth;

    @Column(name = "gender")
    private String gender;

    @OneToMany(mappedBy = "student")
    private List<MedicalCheckupNotification> checkupNotifications;

}
