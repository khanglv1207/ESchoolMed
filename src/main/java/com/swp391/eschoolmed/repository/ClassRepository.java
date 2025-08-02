package com.swp391.eschoolmed.repository;

import com.swp391.eschoolmed.model.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClassRepository extends JpaRepository<ClassEntity, UUID> {
    Optional<ClassEntity> findByClassName(String className);
    Optional<ClassEntity> findByClassNameIgnoreCase(String className);
}
