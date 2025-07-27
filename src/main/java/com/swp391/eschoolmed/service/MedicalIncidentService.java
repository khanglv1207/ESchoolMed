package com.swp391.eschoolmed.service;

import com.swp391.eschoolmed.model.*;
import com.swp391.eschoolmed.repository.*;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.swp391.eschoolmed.dto.request.CreateMedicalIncidentRequest;
import org.thymeleaf.TemplateEngine;

import org.thymeleaf.context.Context;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class MedicalIncidentService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private MedicalIncidentRepository repository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MedicalIncidentNotificationRepository medicalIncidentNotificationRepository;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private NurseRepository  nurseRepository;

    @Autowired
    private ParentStudentRepository  parentStudentRepository;

    public void createIncident(CreateMedicalIncidentRequest request) {
        Student student = studentRepository.findByStudentCode(request.getStudentCode())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học sinh với mã: " + request.getStudentCode()));

        Nurse nurse = nurseRepository.findById(request.getNurseId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy y tá với ID: " + request.getNurseId()));

        ParentStudent parentStudent = parentStudentRepository.findById(request.getParentStudentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mối quan hệ phụ huynh-học sinh với ID: " + request.getParentStudentId()));

        MedicalIncident incident = new MedicalIncident();

        incident.setStudent(student);
        incident.setNurse(nurse);
        incident.setParentStudent(parentStudent);

        incident.setClassName(request.getClassName());
        incident.setOccurredAt(request.getOccurredAt());
        incident.setIncidentType(request.getIncidentType());
        incident.setIncidentDescription(request.getIncidentDescription());

        incident.setInitialTreatment(request.getInitialTreatment());
        incident.setInitialResponder(request.getInitialResponder());

        incident.setHandledByParent(request.isHandledByParent());
        incident.setHandledByStaff(request.isHandledByStaff());
        incident.setMonitoredBySchool(request.isMonitoredBySchool());

        incident.setCurrentStatus(request.getCurrentStatus());
        incident.setImageUrl(request.getImageUrl());

        repository.save(incident);
    }


    public void sendIncidentNotifications() {
        List<MedicalIncidentNotification> unsentList = medicalIncidentNotificationRepository.findAllBySentAtIsNull();
        for (MedicalIncidentNotification notification : unsentList) {
            Parent parent = notification.getParent();
            if (parent == null || parent.getEmail() == null || parent.getEmail().isBlank()) {
                System.out.printf("Bỏ qua - thiếu phụ huynh hoặc email: %d%n", notification.getId());
                continue;
            }
            try {
                MimeMessage message = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setTo(parent.getEmail());
                helper.setFrom(from);
                helper.setSubject("[Emed] Thông báo sự cố y tế");
                Context context = new Context();
                context.setVariable("fullName", parent.getFullName());
                context.setVariable("content", notification.getContent());
                String htmlContent = templateEngine.process("medical-incident-notice.html", context);
                helper.setText(htmlContent, true);
                javaMailSender.send(message);
                notification.setSentAt(LocalDateTime.now());
                medicalIncidentNotificationRepository.save(notification);
                System.out.printf("Đã gửi thông báo đến %s (%s)%n", parent.getFullName(), parent.getEmail());
            } catch (Exception e) {
                System.err.printf("Gửi thất bại đến %s: %s%n", parent.getEmail(), e.getMessage());
            }
        }
    }

}
