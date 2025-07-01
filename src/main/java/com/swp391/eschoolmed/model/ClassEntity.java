package com.swp391.eschoolmed.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "classes")
@Data
public class ClassEntity {
    @Id
    @Column(name = "class_id")
    private UUID classId;

    @Column(name = "class_name")
    private String className;
}
