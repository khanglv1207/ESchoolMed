package com.swp391.eschoolmed.service.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class MailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Autowired
    private TemplateEngine templateEngine;

    @Async
    public void sendNewPassword(String recieverEmail, String fullName, int age) {
        try {
            Context context = new Context();
            context.setVariable("name",fullName);
            context.setVariable("age", age);
            String text = templateEngine.process("greeting.html", context);
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setPriority(1);
            helper.setSubject("Mật Khẩu Mới Của Bạn Đã Sẵn Sàng");
            helper.setFrom(from);
            helper.setTo(recieverEmail);
            helper.setText(text, true);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }


//    public void sendOtp(String receiverEmail, String otpCode) {
//        try {
//            Context context = new Context();
//            context.setVariable("otp",otpCode);
//            String text = templateEngine.process("otp.html", context);
//            MimeMessage message = javaMailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//            helper.setPriority(1);
//            helper.setSubject("OTP của bạn");
//            helper.setFrom(from);
//            helper.setTo(receiverEmail);
//            helper.setText(text, true);
//            javaMailSender.send(message);
//        }catch (MessagingException e){
//            throw new RuntimeException(e);
//        }
//    }
}
