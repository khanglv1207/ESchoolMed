package com.swp391.eschoolmed.controller;

import com.swp391.eschoolmed.service.MedicalIncidentService;
import com.swp391.eschoolmed.dto.request.CreateMedicalIncidentRequest;
import com.swp391.eschoolmed.dto.response.MedicalIncidentResponse;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/api/medical-incidents")
@PreAuthorize("hasAuthority('MEDICAL_STAFF')")
public class MedicalIncidentController {

    @Autowired
    private MedicalIncidentService incidentService;

    @PostMapping
    public ResponseEntity<MedicalIncidentResponse> create(@RequestBody CreateMedicalIncidentRequest request) {
        return ResponseEntity.ok(incidentService.createIncident(request));
    }

    @GetMapping("/student/{id}")
    @PreAuthorize("hasAnyAuthority('MEDICAL_STAFF', 'PARENT')")
    public ResponseEntity<List<MedicalIncidentResponse>> getByStudent(@PathVariable UUID id) {
        return ResponseEntity.ok(incidentService.getIncidentsByStudent(id));
    }
}

