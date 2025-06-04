package com.eschoolmed.eschoolmed.controller;


import com.eschoolmed.eschoolmed.entity.User;
import com.eschoolmed.eschoolmed.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// ✅ Đây là controller dùng cả @Controller (trả về giao diện) và @ResponseBody (REST API)
@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // ======= GIAO DIỆN ========

    // Trang chỉ có nút
    @GetMapping("/page")
    public String usersPage() {
        return "users"; // trả về users.html
    }

    @GetMapping("/all")
    public String showAllUsers(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    // ======= API JSON REST ========
    @GetMapping("/api")
    @ResponseBody
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<User> getUser(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @PostMapping("/api")
    @ResponseBody
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User user) {
        user.setUserId(id);
        return ResponseEntity.ok(userService.updateUser(user));
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("")
    public String redirectToUsersPage() {
        return "redirect:/users/page";
    }
    @GetMapping("/search")
    public String searchUsersByName(@RequestParam("name") String name, Model model) {
        List<User> users = userService.searchUsersByName(name);
        model.addAttribute("users", users);
        model.addAttribute("name", name);
        return "users";
    }
    @GetMapping("/home")
    public String userHome() {
        return "user/home"; // Trả về templates/user/home.html
    }

}
