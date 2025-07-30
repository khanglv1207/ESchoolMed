package com.swp391.eschoolmed.repository;

import com.swp391.eschoolmed.model.VaccineType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface VaccineTypeRepository extends JpaRepository<VaccineType, UUID> {
    @Query("SELECT vt FROM VaccineType vt WHERE upper(trim(vt.name)) = upper(trim(:name))")
    Optional<VaccineType> findByNameIgnoreCaseTrimmed(@Param("name") String name);


}
