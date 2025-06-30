package com.swp391.eschoolmed.repository;

import com.swp391.eschoolmed.model.Parent;
import com.swp391.eschoolmed.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {
    Optional<Student> findStudentByStudentId(UUID studentId);

}
