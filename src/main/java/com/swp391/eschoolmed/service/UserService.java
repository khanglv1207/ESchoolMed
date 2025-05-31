package com.swp391.eschoolmed.service;

import com.swp391.eschoolmed.dto.response.LoginResponse;
import com.swp391.eschoolmed.model.User;
import com.swp391.eschoolmed.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository; // dua repo vao service

    public LoginResponse login(String email, String password){
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isEmpty()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Không được bỏ trống");
        }

        User users = optionalUser.get();
        if(!users.getPassword().equals(password)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sai mật khẩu");
        }


        Optional<User> user =  userRepository.findById(users.getId());
        if(user.isEmpty()){
            throw new RuntimeException("User không tồn tại");
        }

        LoginResponse response = new LoginResponse();
        response.setId(users.getId());
        response.setEmail(users.getEmail());
        response.setFullName(users.getFullName());


        return response;
    }
}
