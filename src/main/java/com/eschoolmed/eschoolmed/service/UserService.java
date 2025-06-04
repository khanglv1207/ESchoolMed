package com.eschoolmed.eschoolmed.service;

import com.eschoolmed.eschoolmed.dto.UserRegisterDto;
import com.eschoolmed.eschoolmed.entity.User;
import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User getUserById(Integer id);
    User createUser(User user);
    User updateUser(User user);
    void deleteUser(Integer id);
    boolean existsByEmail(String email);
    void register(UserRegisterDto userDto);
    List<User> searchUsersByName(String name);

}
