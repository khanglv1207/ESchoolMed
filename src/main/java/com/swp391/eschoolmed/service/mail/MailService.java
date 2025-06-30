package com.swp391.eschoolmed.service.mail;

import com.swp391.eschoolmed.exception.AppException;
import com.swp391.eschoolmed.exception.ErrorCode;
import com.swp391.eschoolmed.model.*;
import com.swp391.eschoolmed.repository.MedicalCheckupNotificationRepository;
import com.swp391.eschoolmed.repository.ParentRepository;
import com.swp391.eschoolmed.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


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
    private MedicalCheckupNotificationRepository medicalCheckupNotificationRepository;

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

        if("password".equalsIgnoreCase(user.getRole())){
            boolean exists = parentRepository.existsByUser(user);
            if(!exists){
                Parent parent = new Parent();
                parent.setUser(user);
                parentRepository.save(parent);
            }
        }
    }

    public void createParentAccount(String email, String fullName, int age) {
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

        Parent parent = new Parent();
        parent.setUser(user);
        parent.setFullName(fullName);
        parent.setEmail(email);
        parent.setPhoneNumber("");
        parent.setAddress("");
        parent.setDateOfBirth("");
        parentRepository.save(parent);

        // Gửi email thông báo
        try {
            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("email", email);
            context.setVariable("tempPassword", tempPassword); // gửi password plaintext cho phụ huynh

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


    public void sendMedicalCheckupNotices(String checkupTitle, String content, LocalDate checkupDate) {
        List<Parent> parents = parentRepository.findAll();

        for (Parent parent : parents) {
            if (parent.getEmail() == null || parent.getEmail().isBlank()) {
                System.out.printf("Bỏ qua parent %s (fullName: %s) vì thiếu email%n",
                        parent.getParentId(), parent.getFullName());
                continue;
            }

            for (ParentStudent ps : parent.getParentStudents()) {
                Student student = ps.getStudent();

                MedicalCheckupNotification notification = new MedicalCheckupNotification();
                notification.setCheckupTitle(checkupTitle);
                notification.setCheckupDate(checkupDate);
                notification.setContent(content);
                notification.setParent(parent);
                notification.setStudent(student);
                notification.setSentAt(LocalDateTime.now());
                notification.setIsConfirmed(false);

                medicalCheckupNotificationRepository.save(notification);

                //tạo email gửi
                try {
                    MimeMessage message = javaMailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                    helper.setTo(parent.getEmail());
                    helper.setFrom(from);
                    helper.setSubject("[Emed] Thông báo kiểm tra y tế định kỳ");
                    String confirmLink = "https://emed.com/parent/checkup/confirm?notificationId=" + notification.getId();
                    Context context = new Context();
                    context.setVariable("fullName", parent.getFullName());
                    context.setVariable("content", content);
                    context.setVariable("confirmLink", confirmLink);
                    String htmlContent = templateEngine.process("medical-checkup-notice.html", context);
                    helper.setText(htmlContent, true);
                    javaMailSender.send(message);
                    System.out.printf("✅ Đã gửi email tới: %s (%s)%n", parent.getFullName(), parent.getEmail());
                } catch (Exception e) {
                    System.err.printf("Không gửi được email cho %s - Lỗi: %s%n",
                            parent.getEmail(), e.getMessage());
                }
            }
        }
    }
}
