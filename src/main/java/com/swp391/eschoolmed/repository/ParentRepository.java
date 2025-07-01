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

    Optional<Parent> findByCode(String code);

    Optional<Parent> findByUserId(UUID userId);

    @Query("SELECT p.code FROM Parent p WHERE p.code IS NOT NULL ORDER BY p.code DESC LIMIT 1")
    String findLatestCode();

    Optional<Parent> findByEmail(String email);

    @Query("SELECT p FROM Parent p WHERE p.user.role = 'parent'")
    List<Parent> findAllRealParents();

    Optional<Parent> findByUser(User user);
}
