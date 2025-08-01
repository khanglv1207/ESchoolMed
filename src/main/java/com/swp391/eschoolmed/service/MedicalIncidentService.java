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
    @Autowired
    private MedicalIncidentRepository medicalIncidentRepository;


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
        List<MedicalIncident> incidents = medicalIncidentRepository.findAllByParentNotifiedFalse();
        for (MedicalIncident incident : incidents) {
            Student student = incident.getStudent();
            ParentStudent parentStudent = incident.getParentStudent();
            if (student == null || parentStudent == null || parentStudent.getParent() == null) {
                System.out.printf("Thiếu dữ liệu cần thiết cho incident ID %d%n", incident.getId());
                continue;
            }
            Parent parent = parentStudent.getParent();
            String parentEmail = parent.getEmail();
            String parentName = parent.getFullName();
            if (parentEmail == null || parentEmail.isBlank()) {
                System.out.printf("Bỏ qua vì thiếu email của phụ huynh: %s%n", parentName);
                continue;
            }

            try {
                MimeMessage message = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setTo(parentEmail);
                helper.setFrom(from);
                helper.setSubject("[eSchoolMed] Thông báo sự cố y tế của học sinh " + student.getFullName());
                Context context = new Context();
                context.setVariable("parentName", parentName);
                context.setVariable("studentName", student.getFullName());
                context.setVariable("incidentType", incident.getIncidentType());
                context.setVariable("incidentDescription", incident.getIncidentDescription());
                context.setVariable("incidentTime", incident.getOccurredAt());
                String html = templateEngine.process("medical-incident-notice.html", context);
                helper.setText(html, true);
                javaMailSender.send(message);
                System.out.printf("Đã gửi thông báo đến %s (%s)%n", parentName, parentEmail);
                MedicalIncidentNotification notification = new MedicalIncidentNotification();
                notification.setIncident(incident);
                notification.setParent(parent);
                notification.setContent(incident.getIncidentDescription());
                notification.setSentAt(LocalDateTime.now());
                medicalIncidentNotificationRepository.save(notification);
                incident.setParentNotified(true);
                medicalIncidentRepository.save(incident);
            } catch (Exception e) {
                System.err.printf("Lỗi khi gửi đến %s (%s): %s%n", parentName, parentEmail, e.getMessage());
            }
        }
    }


}
