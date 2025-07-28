package com.swp391.eschoolmed.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.swp391.eschoolmed.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {

    List<Student> findStudentsNotVaccinatedWith(@Param("vaccineName") String vaccineName);
    Optional<Student> findByStudentCode(String studentCode);

    Optional<Object> findByStudentId(UUID studentId);
}
