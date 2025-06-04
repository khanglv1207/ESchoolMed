package com.eschoolmed.eschoolmed.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserRegisterDto {

    @NotBlank(message = "Email không được bỏ trống")
    @Email(message = "Email không hợp lệ")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@gmail\\.com$", message = "Email phải có đuôi @gmail.com")
    private String email;

    @NotBlank(message = "Mật khẩu không được bỏ trống")
    @Size(min = 4, max = 10, message = "Mật khẩu phải từ 4 đến 10 kí tự")
    private String password;

    @NotBlank(message = "Họ tên không được bỏ trống")
    private String fullName;

    // Constructors
    public UserRegisterDto() {}

    public UserRegisterDto(String email, String password, String fullName) {
        this.email = email;
        this.password = password;
        this.fullName = fullName;
    }

    // Getters & Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
