package com.swp391.eschoolmed.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/home")
    public String homePage() {
        return "home";
    }

    @GetMapping("/health-declaration")
    public String healthDeclarationPage() {
        return "health-declaration";
    }

    @GetMapping("/medical-checkup")
    public String medicalCheckupPage() {
        return "medical-checkup";
    }

    @GetMapping("/vaccination")
    public String vaccinationPage() {
        return "vaccination";
    }

    @GetMapping("/contact")
    public String contactPage() {
        return "contact";
    }

    @GetMapping("/create-parent-account")
    public String createParentAccountPage() {
        return "create-parent-account";
    }

    @GetMapping("/import-students")
    public String importStudentsPage() {
        return "import-students";
    }

    @GetMapping("/admin-dashboard")
    public String adminDashboardPage() {
        return "admin-dashboard";
    }

}
