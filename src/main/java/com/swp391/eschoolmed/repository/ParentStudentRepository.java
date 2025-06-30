package com.swp391.eschoolmed.repository;

import com.swp391.eschoolmed.model.ParentStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ParentStudentRepository extends JpaRepository<ParentStudent, UUID> {
    List<ParentStudent> findAllByParent_ParentId(UUID parentId);
    boolean existsByParent_ParentIdAndStudent_StudentId(UUID parentId, UUID studentId);
}

