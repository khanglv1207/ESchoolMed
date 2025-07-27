package com.swp391.eschoolmed.service.mail;


import java.util.List;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.swp391.eschoolmed.exception.AppException;
import com.swp391.eschoolmed.exception.ErrorCode;
import com.swp391.eschoolmed.model.MedicalCheckupNotification;
import com.swp391.eschoolmed.model.Parent;
import com.swp391.eschoolmed.model.ParentStudent;
import com.swp391.eschoolmed.model.User;
import com.swp391.eschoolmed.repository.MedicalCheckupNotificationRepository;
import com.swp391.eschoolmed.repository.ParentRepository;
import com.swp391.eschoolmed.repository.ParentStudentRepository;
import com.swp391.eschoolmed.repository.StudentRepository;
import com.swp391.eschoolmed.repository.UserRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;


@Service
public class MailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private ParentStudentRepository parentStudentRepository;

    @Autowired
    private MedicalCheckupNotificationRepository medicalCheckupNotificationRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Async

    public void sendNewPassword(String receiverEmail, String fullName, int age, String tempPassword) {
        try {
            Context context = new Context();
            context.setVariable("name", fullName);
            context.setVariable("age", age);
            context.setVariable("password", tempPassword);

            String text = templateEngine.process("greeting.html", context);
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setPriority(1);
            helper.setSubject("Tài khoản eSchoolMed của bạn đã sẵn sàng");
            helper.setFrom(from);
            helper.setTo(receiverEmail);
            helper.setText(text, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    public void changePasswordFirstTime(UUID userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setMustChangePassword(false);
        userRepository.save(user);

        if ("parent".equalsIgnoreCase(user.getRole())) {
            List<ParentStudent> matchingRecords = parentStudentRepository.findByParentEmail(user.getEmail());

            if (matchingRecords.isEmpty()) {
                throw new RuntimeException("Không tìm thấy parent_code tương ứng với email: " + user.getEmail());
            }

            Parent parent = parentRepository.findByEmail(user.getEmail())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy Parent tương ứng với email: " + user.getEmail()));

            parent.setUser(user);
            parent.setFullName(user.getFullName());
            parentRepository.save(parent);

            for (ParentStudent ps : matchingRecords) {
                ps.setParent(parent);

                if (ps.getStudent() == null && ps.getStudentCode() != null) {
                    studentRepository.findByStudentCode(ps.getStudentCode())
                            .ifPresentOrElse(
                                    ps::setStudent,
                                    () -> System.err.printf("Không tìm thấy studentCode: %s%n", ps.getStudentCode())
                            );
                }
            }

            parentStudentRepository.saveAll(matchingRecords);
        }
    }



    public void createParentAccount(String email, String fullName) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        String tempPassword = generateTempPassword();
        String encodedPassword = passwordEncoder.encode(tempPassword);

        User user = new User();
        user.setEmail(email);
        user.setFullName(fullName);
        user.setPasswordHash(encodedPassword);
        user.setRole("parent");
        user.setMustChangePassword(true);
        userRepository.save(user);

        List<ParentStudent> matches = parentStudentRepository.findByParentEmail(email);

        Parent parent;
        if (!matches.isEmpty()) {
            String parentCode = matches.get(0).getParentCode();
            parent = parentRepository.findByCode(parentCode)
                    .orElseGet(() -> {
                        Parent p = new Parent();
                        p.setCode(parentCode);
                        return p;
                    });

            parent.setUser(user);
            parent.setFullName(fullName);
            parent.setEmail(email);
            parent.setPhoneNumber("");
            parent.setAddress("");
            parent.setDateOfBirth(null);
            parentRepository.save(parent);

            for (ParentStudent ps : matches) {
                ps.setParent(parent);
            }
            parentStudentRepository.saveAll(matches);

        } else {
            parent = new Parent();
            parent.setCode(generateNextParentCode());
            parent.setUser(user);
            parent.setFullName(fullName);
            parent.setEmail(email);
            parent.setPhoneNumber("");
            parent.setAddress("");
            parent.setDateOfBirth(null);
            parentRepository.save(parent);
        }
        try {
            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("email", email);
            context.setVariable("tempPassword", tempPassword);

            String text = templateEngine.process("account-created.html", context);
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setSubject("Thông tin tài khoản phụ huynh");
            helper.setFrom(from);
            helper.setTo(email);
            helper.setText(text, true);
            javaMailSender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Gửi email thất bại: " + e.getMessage(), e);
        }
    }


    private String generateTempPassword() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public void sendOtpEmail(String email, String otp) {
        try {
            Context context = new Context();
            context.setVariable("otpCode",otp);
            String text = templateEngine.process("otp.html", context);
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setPriority(1);
            helper.setSubject("Mã otp để reset password");
            helper.setFrom(from);
            helper.setTo(email);
            helper.setText(text, true);
            javaMailSender.send(message);
        }catch (MessagingException e){
            throw new RuntimeException(e);
        }
    }


    private String generateNextParentCode() {
        String latestCode = parentRepository.findLatestCode();
        int next = 1;

        if (latestCode != null && latestCode.startsWith("PH")) {
            try {
                next = Integer.parseInt(latestCode.substring(2)) + 1;
            } catch (NumberFormatException e) {
                next = 1;
            }
        }

        return String.format("PH%06d", next);
    }

    // gửi thông báo kiểm tra sức khỏe
    public void sendMedicalCheckupNotices() {
        List<MedicalCheckupNotification> notifications = medicalCheckupNotificationRepository
                .findAllBySentAtIsNotNullAndIsConfirmedIsNull();
        for (MedicalCheckupNotification notification : notifications) {
            Parent parent = notification.getParent();
            if (parent == null || parent.getEmail() == null || parent.getEmail().isBlank()) {
                System.out.printf("Bỏ qua thông báo vì thiếu phụ huynh hoặc email: %s%n", notification.getId());
                continue;
            }
            try {
                MimeMessage message = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setTo(parent.getEmail());
                helper.setFrom(from);
                helper.setSubject("[Emed] Thông báo kiểm tra y tế định kỳ");
                String confirmLink = "http://localhost:3000/medical-checkup/" + notification.getId();
                Context context = new Context();
                context.setVariable("fullName", parent.getFullName());
                context.setVariable("content", notification.getContent());
                context.setVariable("confirmLink", confirmLink);
                String htmlContent = templateEngine.process("medical-checkup-notice.html", context);
                helper.setText(htmlContent, true);
                javaMailSender.send(message);
                System.out.printf("Đã gửi email tới: %s (%s)%n", parent.getFullName(), parent.getEmail());
            } catch (Exception e) {
                System.err.printf("Gửi thất bại tới %s - Lỗi: %s%n", parent.getEmail(), e.getMessage());
            }
        }
    }



}
