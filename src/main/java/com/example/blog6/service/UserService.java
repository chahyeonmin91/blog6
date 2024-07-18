package com.example.blog6.service;


import com.example.blog6.model.User;
import com.example.blog6.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // findByUsername 메서드 추가
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

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

    // 프로필 이미지 업로드
    public boolean uploadProfileImage(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null && !file.isEmpty()) {
            String uploadDir = "profile-images/";
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            File saveFile = new File(uploadDir, fileName);
            try {
                file.transferTo(saveFile);
                user.setProfileImage("/" + uploadDir + fileName);
                userRepository.save(user);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    //프로필 이미지 삭제 가능
    public boolean deleteProfileImage(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setProfileImage(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // 이메일 주소 변경
    public boolean updateEmail(Long userId, String newEmail) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setEmail(newEmail);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    // 이메일 수신 설정 변경
    public boolean updateEmailNotifications(Long userId, boolean commentNotification, boolean updateNotification) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            user.setEmailNotificationComment(commentNotification);
            user.setEmailNotificationUpdate(updateNotification);
            userRepository.save(user);
            return true;
        }
        return false;
    }
    public boolean validateUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        return user != null && user.getPassword().equals(password);
    }

    public boolean registerUser(Map<String, String> params) {
        try {
            User user = new User();
            user.setUsername(params.get("username"));
            user.setPassword(params.get("password"));
            user.setName(params.get("name"));
            user.setEmail(params.get("email"));
            userRepository.save(user);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}