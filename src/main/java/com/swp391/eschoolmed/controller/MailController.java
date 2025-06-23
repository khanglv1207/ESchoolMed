package com.swp391.eschoolmed.controller;

import com.swp391.eschoolmed.service.mail.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mail")
public class MailController {

    @Autowired
    private MailService mailService;

    @GetMapping("/receive_email")
    void receiveEmail(@RequestParam String receiverEmail,
                      @RequestParam String fullName,
                      @RequestParam int age
    ) {
        mailService.sendNewPassword(receiverEmail, fullName, age);
    }
}
