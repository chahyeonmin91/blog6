package com.example.blog6.controller;


import com.example.blog6.model.Post;
import com.example.blog6.service.PostService;
import com.example.blog6.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @GetMapping("/posts")
    public ResponseEntity<?> getAllPosts() {
        List<Post> posts = postService.getAllPosts();
        List<Map<String, Object>> response = posts.stream().map(post -> Map.of(
                "postId", post.getId().toString(),
                "title", post.getTitle(),
                "author", Map.of(
                        "username", post.getAuthor().getUsername(),
                        "profileImage", post.getAuthor().getProfileImage()
                ),
                "isPublished", post.getPublishStatus(),
                "createdAt", post.getCreatedAt()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) {
        boolean isDeleted = postService.deletePost(postId);
        if (isDeleted) {
            return ResponseEntity.ok(Map.of("message", "글 삭제가 완료되었습니다."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "잘못된 요청입니다."));
        }
    }
}
