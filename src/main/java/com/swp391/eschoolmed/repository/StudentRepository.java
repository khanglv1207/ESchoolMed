package com.swp391.eschoolmed.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.swp391.eschoolmed.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {
    Optional<Student> findStudentByStudentId(UUID studentId);
    Optional<Student> findByStudentCode(String studentCode);
}
