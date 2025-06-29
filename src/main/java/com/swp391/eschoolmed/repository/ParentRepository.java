package com.swp391.eschoolmed.repository;

import com.swp391.eschoolmed.dto.request.ParentProfileRequest;
import com.swp391.eschoolmed.model.Parent;
import com.swp391.eschoolmed.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ParentRepository extends JpaRepository<Parent, UUID> {

    boolean existsByUser(User user);

    Optional<Parent> findByUserId(UUID userId);

}
