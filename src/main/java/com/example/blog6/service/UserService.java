package com.example.blog6.service;

import com.example.blog6.model.User;
import com.example.blog6.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    //api 회원탈퇴
    public boolean deleteUser(Long userId, String password) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null && user.getPassword().equals(password)) {
            userRepository.delete(user);
            return true;
        }
        return false;
    }

    //api 회원정보 수정
    public boolean updateUser(Long userId, Map<String, Object> request) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setName((String) request.get("name"));
            user.setEmail((String) request.get("email"));
            user.setProfileImage((String) request.get("profileImage"));
            user.setBlogTitle((String) request.get("blogTitle"));
            Map<String, Boolean> emailNotification = (Map<String, Boolean>) request.get("emailNotification");
            if (emailNotification != null) {
                user.setEmailNotificationComment(emailNotification.get("comment"));
                user.setEmailNotificationUpdate(emailNotification.get("update"));
            }
            userRepository.save(user);
            return true;
        }
        return false;
    }
}