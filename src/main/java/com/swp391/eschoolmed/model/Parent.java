package com.swp391.eschoolmed.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Table(name = "parents")
@Entity
public class Parent {

    @Id
    @Column(name = "parent_id")
    private UUID parentId;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", unique = true)
    private User user;

    @Column(name = "full_name")
    private String fullName;

    @Column
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "address")
    private String address;

    @OneToMany(mappedBy = "parent")
    private List<MedicalCheckupNotification> checkupNotifications;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<ParentStudent> parentStudents;

    @Column(unique = true, length = 10)
    private String code;

    @OneToMany(mappedBy = "parent")
    private Collection<MedicalIncidentNotification> medicalIncidentNotification;

}
