package com.example.blog6.controller;

import com.example.blog6.model.Image;
import com.example.blog6.model.Post;
import com.example.blog6.repository.ImageRepository;
import com.example.blog6.repository.PostRepository;
import com.example.blog6.service.PostService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ImageRepository imageRepository;

    @GetMapping("/post")
    public Page<Post> getPosts(@RequestParam String sort, Pageable pageable) {
        return postService.getPosts(sort, pageable);
    }

    @PostMapping("/post")
    public void savePost(@RequestParam Long postId, @RequestParam String title, @RequestParam String tags,
                         @RequestParam String content, @RequestParam Boolean publishStatus) {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            post.setTitle(title);
            post.setTags(tags);
            post.setContent(content);
            post.setPublishStatus(publishStatus);
            postRepository.save(post);
        }
    }

    @PostMapping("/uploadImage")
    public String uploadImage(@RequestParam("image") MultipartFile file, @RequestParam("postId") Long postId, HttpServletRequest request) {
        if (!file.isEmpty()) {
            String uploadDir = "uploads/";
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            File saveFile = new File(uploadDir, fileName);
            try {
                file.transferTo(saveFile);
                Image image = new Image();
                image.setPostId(postId);
                image.setUrl("/" + uploadDir + fileName);
                imageRepository.save(image);
                return "/" + uploadDir + fileName;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    @PostMapping("/saveDraft")
    public Long saveDraft(@RequestParam String title, @RequestParam String tags, @RequestParam String content) {
        Post post = new Post();
        post.setTitle(title);
        post.setTags(tags);
        post.setContent(content);
        post.setPublishStatus(false);
        postRepository.save(post);
        return post.getId();
    }
}