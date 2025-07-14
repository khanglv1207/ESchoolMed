package com.swp391.eschoolmed.service;

import com.swp391.eschoolmed.model.Parent;
import org.springframework.stereotype.Service;
import com.swp391.eschoolmed.model.Student;
import com.swp391.eschoolmed.model.MedicalIncident;

@Service
public class NotificationService {

    public void notifyParent(Student student, MedicalIncident incident) {
        String parentEmail = student.getParent().getEmail(); // nếu có liên kết parent
        String message = String.format("Thông báo y tế: học sinh %s gặp sự cố: %s - %s",
                student.getFullName(), incident.getIncidentType(), incident.getDescription());

        System.out.println("Gửi thông báo tới phụ huynh " + parentEmail + ": " + message);

        // Bạn có thể tích hợp: FCM, EmailService, Twilio...
    }
}

