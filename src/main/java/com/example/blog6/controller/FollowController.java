package com.example.blog6.controller;


import com.example.blog6.model.User;
import com.example.blog6.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class FollowController {

    @Autowired
    private FollowService followService;

    //팔로우
    @PostMapping("/{username}/follow")
    public ResponseEntity<?> followUser(@PathVariable String username, Principal principal) {
        String followerUsername = principal.getName();
        boolean isFollowed = followService.followUser(followerUsername, username);
        if (isFollowed) {
            return ResponseEntity.ok(Map.of("message", "팔로우가 완료되었습니다."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "잘못된 요청입니다."));
        }
    }

    //팔로우 취소
    @PostMapping("/{username}/unfollow")
    public ResponseEntity<?> unfollowUser(@PathVariable String username, Principal principal) {
        String followerUsername = principal.getName();
        boolean isUnfollowed = followService.unfollowUser(followerUsername, username);
        if (isUnfollowed) {
            return ResponseEntity.ok(Map.of("message", "언팔로우가 완료되었습니다."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "잘못된 요청입니다."));
        }
    }

    //팔로우 목록
    @GetMapping("/followers")
    public ResponseEntity<?> getFollowers(Principal principal) {
        String username = principal.getName();
        List<User> followers = followService.getFollowers(username);
        if (followers != null) {
            List<Map<String, Object>> response = followers.stream().map(user -> Map.of(
                    "userId", (Object) user.getId().toString(),
                    "username", (Object) user.getUsername(),
                    "profileImage", (Object) user.getProfileImage()
            )).collect(Collectors.toList());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "잘못된 요청입니다."));
        }
    }
}