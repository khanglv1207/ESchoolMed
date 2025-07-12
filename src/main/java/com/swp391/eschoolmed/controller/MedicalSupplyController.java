package com.swp391.eschoolmed.controller;

import com.swp391.eschoolmed.dto.request.CreateMedicalSupplyRequest;
import com.swp391.eschoolmed.dto.response.MedicalSupplyResponse;
import com.swp391.eschoolmed.service.MedicalSupplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/supplies")
public class MedicalSupplyController {

    @Autowired private MedicalSupplyService supplyService;

    @PostMapping
    public ResponseEntity<MedicalSupplyResponse> create(@RequestBody CreateMedicalSupplyRequest request) {
        return ResponseEntity.ok(supplyService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<MedicalSupplyResponse>> getAll() {
        return ResponseEntity.ok(supplyService.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        supplyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
