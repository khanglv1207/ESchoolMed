package com.swp391.eschoolmed.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.swp391.eschoolmed.dto.request.CreateMedicalIncidentRequest;
import com.swp391.eschoolmed.dto.response.MedicalIncidentResponse;
import com.swp391.eschoolmed.model.MedicalIncident;
import com.swp391.eschoolmed.model.Student;
import com.swp391.eschoolmed.model.User;
import com.swp391.eschoolmed.repository.MedicalIncidentRepository;
import com.swp391.eschoolmed.repository.StudentRepository;
import com.swp391.eschoolmed.repository.UserRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MedicalIncidentService {

    @Autowired private MedicalIncidentRepository incidentRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private NotificationService notificationService;

    public MedicalIncidentResponse createIncident(CreateMedicalIncidentRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Học sinh không tồn tại"));

        User staff = userRepository.findById(request.getStaffId())
                .orElseThrow(() -> new RuntimeException("Nhân viên y tế không tồn tại"));

        MedicalIncident incident = new MedicalIncident();
        incident.setStudent(student);
        incident.setStaff(staff);
        incident.setIncidentType(request.getIncidentType());
        incident.setDescription(request.getDescription());
        incident.setOccurredAt(request.getOccurredAt());
        incident.setParentNotified(true); // Gửi ngay khi ghi nhận

        incidentRepository.save(incident);

        // Gửi thông báo
        notificationService.notifyParent(student, incident);

        return convertToResponse(incident);
    }

    private MedicalIncidentResponse convertToResponse(MedicalIncident incident) {
        MedicalIncidentResponse res = new MedicalIncidentResponse();
        res.setStudentName(incident.getStudent().getFullName());
        res.setIncidentType(incident.getIncidentType());
        res.setDescription(incident.getDescription());
        res.setOccurredAt(incident.getOccurredAt());
        res.setStaffName(incident.getStaff().getFullName());
        return res;
    }

    public List<MedicalIncidentResponse> getIncidentsByStudent(UUID studentId) {
        List<MedicalIncident> incidents = incidentRepository.findByStudent_StudentId(studentId);
        return incidents.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
}
