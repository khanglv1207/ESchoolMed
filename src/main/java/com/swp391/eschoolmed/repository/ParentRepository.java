package com.swp391.eschoolmed.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.swp391.eschoolmed.model.Parent;
import com.swp391.eschoolmed.model.User;

@Repository
public interface ParentRepository extends JpaRepository<Parent, UUID> {

    Optional<Parent> findByUser_Id(UUID userId);

    Optional<Parent> findByCode(String code);

    Optional<Parent> findByUserId(UUID userId);

    @Query("SELECT p.code FROM Parent p WHERE p.code IS NOT NULL ORDER BY p.code DESC LIMIT 1")
    String findLatestCode();

    Optional<Parent> findByEmail(String email);

    Optional<Parent> findByUser(User user);

    Optional<Parent> findByUser_id(UUID id);



}
