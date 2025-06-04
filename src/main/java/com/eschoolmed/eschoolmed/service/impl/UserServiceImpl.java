package com.eschoolmed.eschoolmed.service.impl;

import com.eschoolmed.eschoolmed.dto.UserRegisterDto;
import com.eschoolmed.eschoolmed.entity.User;
import com.eschoolmed.eschoolmed.repository.UserRepository;
import com.eschoolmed.eschoolmed.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // ✅ Inject đúng cách

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll(Sort.by(Sort.Direction.ASC, "userId"));
    }

    @Override
    public User getUserById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<User> searchUsersByName(String keyword) {
        return userRepository.findByFullNameContainingIgnoreCase(keyword);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public void register(UserRegisterDto userDto) {
        User user = new User();
        user.setEmail(userDto.getEmail());
        user.setFullName(userDto.getFullName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword())); // ✅ Mã hóa mật khẩu
        user.setRole("User"); // Đặt role mặc định

        userRepository.save(user); // ✅ Lưu vào CSDL
    }
}
