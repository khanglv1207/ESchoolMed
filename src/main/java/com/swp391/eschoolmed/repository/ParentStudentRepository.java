package com.swp391.eschoolmed.repository;

import com.swp391.eschoolmed.model.ParentStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ParentStudentRepository extends JpaRepository<ParentStudent, UUID> {
    List<ParentStudent> findAllByParent_ParentId(UUID parentId);
    List<ParentStudent> findByParentEmail(String email);
    @Query("SELECT MAX(ps.parentCode) FROM ParentStudent ps WHERE ps.parentCode LIKE 'PH%'")
    String findLatestParentCode();

    @Query("SELECT MAX(ps.studentCode) FROM ParentStudent ps WHERE ps.studentCode LIKE 'HS%'")
    String findLatestStudentCode();
    List<ParentStudent> findAllByParentCode(String code);
}

