package com.eschoolmed.eschoolmed.repository;

import java.util.Optional;
import java.util.List;
import com.eschoolmed.eschoolmed.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    List<User> findByFullNameContainingIgnoreCase(String fullName);
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
