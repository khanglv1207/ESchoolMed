package com.swp391.eschoolmed.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.swp391.eschoolmed.model.ParentStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.swp391.eschoolmed.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student, UUID> {
    @Query("""
    SELECT s FROM Student s
    LEFT JOIN s.healthProfile hp
    WHERE LOWER(hp.vaccinationRecord) NOT LIKE LOWER(CONCAT('%', :vaccineName, '%'))
       OR hp.vaccinationRecord IS NULL
""")
    List<Student> findEligibleStudentsByVaccine(@Param("vaccineName") String vaccineName);

    List<ParentStudent> findByParent_ParentId(UUID parentId);

    Optional<Student> findByStudentCode(String studentCode);

}
