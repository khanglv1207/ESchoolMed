package com.swp391.eschoolmed.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.swp391.eschoolmed.model.Parent;
import com.swp391.eschoolmed.model.User;

@Repository
public interface ParentRepository extends JpaRepository<Parent, UUID> {

    boolean existsByUser(User user);

    Optional<Parent> findByUserId(UUID userId);

    @Query("SELECT p.code FROM Parent p WHERE p.code IS NOT NULL ORDER BY p.code DESC LIMIT 1")
    String findLatestCode();

    Optional<Parent> findByEmail(String email);
}
