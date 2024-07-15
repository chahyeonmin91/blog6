package com.example.blog6.controller;

import com.example.blog6.Util.JwtUtil;
import com.example.blog6.Util.UserContext;
import com.example.blog6.model.User;
import com.example.blog6.repository.UserRepository;
import com.example.blog6.service.BlogService;
import com.example.blog6.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private BlogService blogService;


    //api 회원탈퇴
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId, @RequestBody Map<String, String> request) {
        String password = request.get("password");
        boolean isDeleted = userService.deleteUser(userId, password);
        if (isDeleted) {
            return ResponseEntity.ok(Map.of("message", "회원 탈퇴가 완료되었습니다."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "비밀번호가 잘못되었습니다."));
        }
    }

    //API 회원정보수정
    @PutMapping("/users/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody Map<String, Object> request) {
        boolean isUpdated = userService.updateUser(userId, request);
        if (isUpdated) {
            return ResponseEntity.ok(Map.of("message", "회원 정보가 수정되었습니다."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "잘못된 요청입니다."));
        }
    }


    // api 명세 get loginform
    @GetMapping("/loginform")
    public String loginForm() {
        return "login";
    }



    // api 명세 POST login
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpServletResponse response,
                        Model model) {
        if ("validUser".equals(username) && "validPassword".equals(password)) {
            // 로그인 성공 시
            String token = jwtUtil.generateToken(username);
            response.setHeader("Authorization", "Bearer " + token);
            return "redirect:/" + username;
        } else {
            // 로그인 실패 시
            model.addAttribute("error", "Invalid username or password.");
            return "login";
        }
    }



    // api 명세 get username
    @GetMapping("/@{username}")
    public String userPage(@PathVariable String username, HttpServletRequest request, Model model) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.validateToken(token)) {
                String tokenUsername = jwtUtil.getUsernameFromToken(token);
                if (username.equals(tokenUsername)) {
                    model.addAttribute("username", username);
                    return "blog";
                }
            }
        }
        return "redirect:/loginform";
    }

    //특정 사용자 블로그 페이지 조회
    @GetMapping("/{username}/posts")
    public ResponseEntity<?> getUserBlogPage(@PathVariable String username) {
        Map<String, Object> blogPage = blogService.getUserBlogPage(username);
        if (blogPage != null) {
            return ResponseEntity.ok(blogPage);
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "잘못된 요청입니다."));
        }
    }


    //main
    @GetMapping("/main")
    public String mainPage(Model model) {
        String currentUser = UserContext.getUser();
        if (currentUser != null) {
            User user = userRepository.findByUsername(currentUser);
            if (user != null) {
                model.addAttribute("username", user.getUsername());
                model.addAttribute("profileImage", user.getProfileImage());
            } else {
                // 사용자 정보를 찾을 수 없을 경우 처리
                return "redirect:/loginform";
            }
        } else {
            // currentUser가 null일 경우 처리 (로그인하지 않은 경우)
            return "redirect:/loginform";
        }
        return "main";
    }




    //Filter 관련
    @GetMapping("/user-info")
    public String getUserInfo() {
        String userId = UserContext.getUser();
        if (userId != null) {
            return "User ID: " + userId;
        } else {
            return "No user authenticated";
        }
    }

}
