package com.swp391.eschoolmed.repository;

import com.swp391.eschoolmed.model.MedicalSupply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MedicalSupplyRepository extends JpaRepository<MedicalSupply, UUID> {
}
