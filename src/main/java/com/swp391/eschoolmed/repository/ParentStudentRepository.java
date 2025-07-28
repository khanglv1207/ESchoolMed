package com.swp391.eschoolmed.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.swp391.eschoolmed.model.ParentStudent;

@Repository
public interface ParentStudentRepository extends JpaRepository<ParentStudent, UUID> {
    List<ParentStudent> findAllByParent_ParentId(UUID parentId);
    List<ParentStudent> findByParentEmail(String email);
    @Query("SELECT MAX(ps.parentCode) FROM ParentStudent ps WHERE ps.parentCode LIKE 'PH%'")
    String findLatestParentCode();

    @Query("SELECT MAX(ps.studentCode) FROM ParentStudent ps WHERE ps.studentCode LIKE 'HS%'")
    String findLatestStudentCode();

    Optional<ParentStudent> findByParent_ParentIdAndStudent_StudentId(UUID parentParentId, UUID studentStudentId);

    Optional<ParentStudent> findFirstByStudent_StudentId(UUID studentId);

    List<ParentStudent> findByParent_ParentId(UUID parentId);



}

