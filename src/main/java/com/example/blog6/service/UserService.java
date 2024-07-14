package com.example.blog6.service;

import com.example.blog6.model.User;
import com.example.blog6.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public boolean deleteUser(Long userId, String password) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null && user.getPassword().equals(password)) {
            userRepository.delete(user);
            return true;
        }
        return false;
    }
}