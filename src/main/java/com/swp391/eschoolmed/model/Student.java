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

    @Column(name = "student_code")
    private String studentCode;

    @Column(name = "full_name")
    private String fullName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "parent_id", insertable = false, updatable = false)
    private Parent parent;

    // Giữ lại class_id nếu bạn cần thao tác thủ công
    @Column(name = "class_id")
    private UUID class_id;

    // Liên kết với bảng lớp học (ClassEntity)
    @ManyToOne(fetch = FetchType.LAZY) // hoặc EAGER nếu bạn muốn tự động load
    @JoinColumn(name = "class_id", referencedColumnName = "class_id", insertable = false, updatable = false)
    private ClassEntity classEntity;

    @Column(name = "Date_of_birth")
    private LocalDate Date_of_birth;

    @Column(name = "gender")
    private String gender;
}
