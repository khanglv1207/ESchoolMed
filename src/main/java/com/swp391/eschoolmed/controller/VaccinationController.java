package com.swp391.eschoolmed.controller;

import com.swp391.eschoolmed.dto.request.CreateVaccinationRecordRequest;
import com.swp391.eschoolmed.dto.response.VaccinationRecordResponse;
import com.swp391.eschoolmed.service.VaccinationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/vaccinations")
public class VaccinationController {

    private final VaccinationService vaccinationService;

    @Autowired
    public VaccinationController(VaccinationService vaccinationService) {
        this.vaccinationService = vaccinationService;
    }

    // Tạo bản ghi tiêm chủng mới
    @PostMapping
    public ResponseEntity<VaccinationRecordResponse> create(@RequestBody CreateVaccinationRecordRequest request) {
        VaccinationRecordResponse response = vaccinationService.createRecord(request);
        return ResponseEntity.ok(response);
    }

    // Lấy toàn bộ danh sách tiêm chủng
    @GetMapping
    public ResponseEntity<List<VaccinationRecordResponse>> getAll() {
        return ResponseEntity.ok(vaccinationService.getAll());
    }

    // Lấy bản ghi theo học sinh (UUID)
    @GetMapping("/student/{id}")
    public ResponseEntity<List<VaccinationRecordResponse>> getByStudent(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(vaccinationService.getByStudent(id));
    }

    // Xoá bản ghi theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        vaccinationService.deleteRecord(id);
        return ResponseEntity.noContent().build();
    }
}
