package com.swp391.eschoolmed.repository;

import com.swp391.eschoolmed.model.MedicationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MedicationItemRepository extends JpaRepository<MedicationItem, UUID> {
    List<MedicationItem> findByRequest_RequestId(UUID requestId);
}
