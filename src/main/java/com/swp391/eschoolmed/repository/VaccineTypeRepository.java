package com.swp391.eschoolmed.repository;

import com.swp391.eschoolmed.model.VaccineType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VaccineTypeRepository extends JpaRepository<VaccineType, UUID> {
}
