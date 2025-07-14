package com.swp391.eschoolmed.repository;

import com.swp391.eschoolmed.model.Vaccine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VaccineRepository extends JpaRepository<Vaccine, Long> {

}
