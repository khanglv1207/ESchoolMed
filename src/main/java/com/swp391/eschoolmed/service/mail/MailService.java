package com.swp391.eschoolmed.service.mail;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import java.util.UUID;

import com.swp391.eschoolmed.dto.request.*;
import com.swp391.eschoolmed.model.*;
import com.swp391.eschoolmed.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.swp391.eschoolmed.exception.AppException;
import com.swp391.eschoolmed.exception.ErrorCode;

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
    @Autowired
    private VaccineTypeRepository vaccineTypeRepository;
    @Autowired
    private VaccinationNotificationRepository vaccinationNotificationRepository;
    @Autowired
    private VaccinationResultRepository vaccinationResultRepository;
    @Autowired
    private VaccinationConfirmationRepository vaccinationConfirmationRepository;


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


    public void sendVaccinationNotices(VaccinationNotificationRequest request) {
        VaccineType vaccineType = vaccineTypeRepository.findByNameIgnoreCaseTrimmed(request.getVaccineName())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy loại vaccine: " + request.getVaccineName()));

        List<Student> students = studentRepository.findAllById(request.getStudentIds());

        for (Student student : students) {
            boolean alreadySent = vaccinationNotificationRepository.existsByStudentAndVaccineType(student, vaccineType);
            if (alreadySent) {
                System.out.printf("Đã tồn tại thông báo tiêm cho học sinh: %s%n", student.getFullName());
                continue;
            }
            List<ParentStudent> parentLinks = parentStudentRepository.findByStudent_StudentId(student.getStudentId());
            if (parentLinks == null || parentLinks.isEmpty()) {
                System.out.printf("Bỏ qua học sinh %s vì không có phụ huynh liên kết.%n", student.getFullName());
                continue;
            }
            VaccinationNotification notification = VaccinationNotification.builder()
                    .vaccineType(vaccineType)
                    .scheduledDate(request.getScheduledDate().atStartOfDay())
                    .location(request.getLocation())
                    .note(request.getNote())
                    .createdAt(LocalDateTime.now())
                    .build();
            vaccinationNotificationRepository.save(notification);

            for (ParentStudent link : parentLinks) {
                String parentEmail = link.getParentEmail();
                String parentName = link.getParentName();

                if (parentEmail == null || parentEmail.isBlank()) {
                    System.out.printf("Bỏ qua phụ huynh %s vì thiếu email.%n", parentName);
                    continue;
                }

                VaccinationConfirmation confirmation = VaccinationConfirmation.builder()
                        .student(student)
                        .notification(notification)
                        .status(ConfirmationStatus.PENDING)
                        .confirmedAt(LocalDateTime.now())
                        .build();
                vaccinationConfirmationRepository.save(confirmation);
                try {
                    MimeMessage message = javaMailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                    helper.setTo(parentEmail);
                    helper.setFrom(from);
                    helper.setSubject("[Emed] Thông báo tiêm chủng cho học sinh");
                    String confirmLink = "http://localhost:3000/vaccination-confirm/" + confirmation.getId();
                    Context context = new Context();
                    context.setVariable("fullName", parentName);
                    context.setVariable("studentName", student.getFullName());
                    context.setVariable("vaccineName", vaccineType.getName());
                    context.setVariable("scheduledDate", request.getScheduledDate().toString());
                    context.setVariable("location", request.getLocation());
                    context.setVariable("note", request.getNote());
                    context.setVariable("confirmLink", confirmLink);
                    String htmlContent = templateEngine.process("vaccination-notice.html", context);
                    helper.setText(htmlContent, true);
                    javaMailSender.send(message);
                    System.out.printf("Đã gửi thông báo tiêm cho: %s (%s)%n", parentName, parentEmail);
                } catch (Exception e) {
                    System.err.printf("Gửi thất bại tới %s - Lỗi: %s%n", parentEmail, e.getMessage());
                }
            }
        }
    }


    // gửi thông báo kiểm tra sức khỏe
    @Transactional
    public void sendBroadcastMedicalCheckup(MedicalCheckupEmailRequest request) {
        List<ParentStudent> targets = parentStudentRepository.findAll();
        for (ParentStudent ps : targets) {
            if (ps.getParentEmail() == null || ps.getParentEmail().isBlank()) continue;
            MedicalCheckupNotification notification = MedicalCheckupNotification.builder()
                    .checkupTitle(request.getCheckupTitle())
                    .checkupDate(request.getCheckupDate())
                    .content(request.getContent())
                    .student(ps.getStudent())
                    .parent(ps.getParent())
                    .studentName(ps.getStudentName())
                    .className(ps.getClassName())
                    .gender(ps.getGender())
                    .sentAt(LocalDateTime.now())
                    .isConfirmed(false)
                    .build();
            medicalCheckupNotificationRepository.save(notification);
            try {
                MimeMessage message = javaMailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setTo(ps.getParentEmail());
                helper.setFrom(from);
                helper.setSubject("[Emed] " + request.getCheckupTitle());
                String confirmLink = "http://localhost:3000/medical-checkup/" + notification.getId();
                Context context = new Context();
                context.setVariable("fullName", ps.getParentName());
                context.setVariable("studentName", ps.getStudentName());
                context.setVariable("className", ps.getClassName());
                context.setVariable("content", request.getContent());
                context.setVariable("confirmLink", confirmLink);
                String htmlContent = templateEngine.process("medical-checkup-notice.html", context);
                helper.setText(htmlContent, true);
                javaMailSender.send(message);
            } catch (Exception e) {
                System.err.printf("Lỗi gửi đến %s: %s%n", ps.getParentEmail(), e.getMessage());
            }
        }
    }


    public void sendVaccinationResultsToParents() {
        List<VaccinationResult> results = vaccinationResultRepository.findAllByFinalizedFalse();
        for (VaccinationResult result : results) {
            if (!result.isSuccessful()) {
                System.out.printf("Bỏ qua kết quả vì không thành công: %s%n", result.getId());
                continue;
            }
            VaccinationConfirmation confirmation = result.getConfirmation();
            Student student = confirmation.getStudent();
            List<ParentStudent> parentLinks = parentStudentRepository.findByStudent(student);
            if (parentLinks.isEmpty()) {
                System.out.printf("Không tìm thấy phụ huynh cho học sinh: %s%n", student.getFullName());
                continue;
            }
            for (ParentStudent ps : parentLinks) {
                String parentEmail = ps.getParentEmail();
                String parentName = ps.getParentName();
                if (parentEmail == null || parentEmail.isBlank()) {
                    System.out.printf("Bỏ qua vì thiếu email phụ huynh: %s (%s)%n", parentName, student.getFullName());
                    continue;
                }
                try {
                    MimeMessage message = javaMailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                    helper.setTo(parentEmail);
                    helper.setSubject("[eSchoolMed] Kết quả tiêm chủng cho " + student.getFullName());
                    helper.setFrom("no-reply@eschoolmed.vn");
                    Context context = new Context();
                    context.setVariable("parentName", parentName);
                    context.setVariable("studentName", ps.getStudentName());
                    context.setVariable("vaccine", confirmation.getNotification().getVaccineType().getName());
                    context.setVariable("date", result.getActualVaccinationDate());
                    context.setVariable("reactionNote", result.getReactionNote());
                    context.setVariable("needsBooster", result.isNeedsBooster());
                    String html = templateEngine.process("vaccination-result.html", context);
                    helper.setText(html, true);
                    javaMailSender.send(message);
                    System.out.printf("Đã gửi email tới: %s (%s)%n", parentName, parentEmail);
                } catch (Exception e) {
                    System.err.printf("Lỗi khi gửi tới %s (%s): %s%n", parentName, parentEmail, e.getMessage());
                }
            }
            result.setFinalized(true);
            vaccinationResultRepository.save(result);
        }


    }

}
