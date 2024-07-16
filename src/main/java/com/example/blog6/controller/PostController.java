package com.example.blog6.controller;

import com.example.blog6.model.Image;
import com.example.blog6.model.Post;
import com.example.blog6.model.Series;
import com.example.blog6.model.User;
import com.example.blog6.repository.ImageRepository;
import com.example.blog6.repository.PostRepository;
import com.example.blog6.repository.SeriesRepository;
import com.example.blog6.service.PostService;

import com.example.blog6.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private SeriesRepository seriesRepository;

    @GetMapping("/post")
    public Page<Post> getPosts(@RequestParam String sort, Pageable pageable) {
        return postService.getPosts(sort, pageable);
    }


    @PostMapping("/post")
    public void savePost(@RequestParam Long postId, @RequestParam String title, @RequestParam String tags,
                         @RequestParam String content, @RequestParam Boolean publishStatus,
                         @RequestParam(required = false) MultipartFile previewImage,
                         @RequestParam(required = false) Long seriesId) throws IOException {
        Optional<Post> postOptional = postRepository.findById(postId);
        if (postOptional.isPresent()) {
            Post post = postOptional.get();
            post.setTitle(title);
            post.setTags(tags);
            post.setContent(content);
            post.setPublishStatus(publishStatus);

            if (previewImage != null && !previewImage.isEmpty()) {
                String uploadDir = "preview-images/";
                String fileName = UUID.randomUUID().toString() + "_" + previewImage.getOriginalFilename();
                File saveFile = new File(uploadDir, fileName);
                previewImage.transferTo(saveFile);
                post.setPreviewImage("/" + uploadDir + fileName);
            }

            if (seriesId != null) {
                Series series = seriesRepository.findById(seriesId).orElse(null);
                post.setSeries(series);
            }

            postRepository.save(post);
        }
    }

    @GetMapping("/{username}/posts/{title}")
    public ResponseEntity<?> getPost(@PathVariable String username, @PathVariable String title) {
        String decodedTitle = URLDecoder.decode(title, StandardCharsets.UTF_8);
        User user = userService.findByUsername(username);
        if (user != null) {
            Post post = postRepository.findByUserAndTitle(user, decodedTitle);
            if (post != null) {
                return ResponseEntity.ok(post);
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "글을 찾을 수 없습니다."));
    }

    // 새로운 엔드포인트 추가: 포스트 출간
    @PostMapping("/{username}/posts/publish")
    public ResponseEntity<?> publishPost(@PathVariable String username, @RequestParam Long postId,
                                         @RequestParam Boolean publishStatus, @RequestParam String title,
                                         @RequestParam String content, @RequestParam(required = false) MultipartFile previewImage,
                                         @RequestParam(required = false) Long seriesId) throws IOException {
        User user = userService.findByUsername(username);
        if (user != null) {
            Optional<Post> postOptional = postRepository.findById(postId);
            if (postOptional.isPresent()) {
                Post post = postOptional.get();
                post.setTitle(title);
                post.setContent(content);
                post.setPublishStatus(publishStatus);

                // 미리보기 이미지 저장 로직 추가
                if (previewImage != null && !previewImage.isEmpty()) {
                    String uploadDir = "preview-images/";
                    String fileName = UUID.randomUUID().toString() + "_" + previewImage.getOriginalFilename();
                    File saveFile = new File(uploadDir, fileName);
                    previewImage.transferTo(saveFile);
                    post.setPreviewImage("/" + uploadDir + fileName);
                }

                // 시리즈 추가 로직 추가
                if (seriesId != null) {
                    Series series = seriesRepository.findById(seriesId).orElse(null);
                    post.setSeries(series);
                }

                postRepository.save(post);
                String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8); // URL 인코딩
                return ResponseEntity.ok(Map.of("url", "/@" + username + "/posts/" + encodedTitle));
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "잘못된 요청입니다."));
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


    // 글 삭제 (임시 글 포함)
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post != null && !post.getPublishStatus()) {
            postRepository.delete(post);
            return ResponseEntity.ok(Map.of("message", "글 삭제가 완료되었습니다."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "잘못된 요청입니다."));
        }
    }

    //글 목록 조회(임시글 포함)
    @GetMapping("/posts/drafts")
    public ResponseEntity<?> getDraftPosts() {
        List<Post> posts = postService.getAllPostsIncludingDrafts();
        List<Map<String, Object>> response = posts.stream().map(post -> Map.of(
                "postId", (Object) post.getId().toString(),
                "title", (Object) post.getTitle(),
                "createdAt", (Object) post.getCreatedAt().toString()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    //최신글 인기글
    @GetMapping("/posts")
    public ResponseEntity<?> getPosts(@RequestParam String sort) {
        List<Post> posts = postService.getPosts(sort);
        List<Map<String, Object>> response = posts.stream().map(post -> Map.of(
                "postId", post.getId().toString(),
                "title",  post.getTitle(),
                "excerpt", post.getExcerpt(),
                "author", Map.of(
                        "username", post.getUser().getUsername(),
                        "profileImage", post.getUser().getProfileImage()
                ),
                "likes", post.getLikes()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
    // 좋아요한 글들 조회
    @GetMapping("/posts/liked")
    public ResponseEntity<?> getLikedPosts(Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username); // 추가된 코드
        List<Post> posts = postService.getLikedPosts(user);
        List<Map<String, Object>> response = posts.stream().map(post -> Map.of(
                "postId", post.getId().toString(),
                "title", post.getTitle(),
                "excerpt", post.getExcerpt(),
                "author", Map.of(
                        "username", post.getUser().getUsername(),
                        "profileImage", post.getUser().getProfileImage()
                ),
                "likes", post.getLikes()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // 사용자가 읽은 글들 조회
    @GetMapping("/posts/read")
    public ResponseEntity<?> getReadPosts(Principal principal) {
        String username = principal.getName();
        User user = userService.findByUsername(username); // 추가된 코드
        List<Post> posts = postService.getReadPosts(user);
        List<Map<String, Object>> response = posts.stream().map(post -> Map.of(
                "postId", post.getId().toString(),
                "title", post.getTitle(),
                "excerpt", post.getExcerpt(),
                "author", Map.of(
                        "username", post.getUser().getUsername(),
                        "profileImage", post.getUser().getProfileImage()
                ),
                "likes", post.getLikes()
        )).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    //블로그 글보기
    @GetMapping("/posts/{postId}")
    public ResponseEntity<?> viewPost(@PathVariable Long postId, Principal principal) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "글을 찾을 수 없습니다."));
        }

        String username = principal.getName();
        boolean isOwner = post.getAuthor().getUsername().equals(username);

        Map<String, Object> response = new HashMap<>();
        response.put("postId", post.getId());
        response.put("title", post.getTitle());
        response.put("content", post.getContent());
        response.put("tags", post.getTags());
        response.put("createdAt", post.getCreatedAt());
        response.put("isPublished", post.getPublishStatus());
        response.put("author", Map.of(
                "username", post.getAuthor().getUsername(),
                "profileImage", post.getAuthor().getProfileImage()
        ));
        response.put("isOwner", isOwner);
        response.put("canLike", !isOwner);
        response.put("canEdit", isOwner);
        response.put("canDelete", isOwner);
        response.put("canFollow", !isOwner);
        response.put("commentCount", post.getComments().size());

        return ResponseEntity.ok(response);
    }

    // 내 글의 경우 통계정보
    @GetMapping("/posts/{postId}/stats")
    public ResponseEntity<?> getPostStats(@PathVariable Long postId, Principal principal) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "글을 찾을 수 없습니다."));
        }

        String username = principal.getName();
        if (!post.getAuthor().getUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "권한이 없습니다."));
        }

        // 통계 정보 예시 (구현 필요)
        Map<String, Object> stats = Map.of(
                "views", post.getViews(),
                "likes", post.getLikes(),
                "comments", post.getComments().size()
        );

        return ResponseEntity.ok(stats);
    }

    //이전/다음 글 링크 제공
    @GetMapping("/posts/{postId}/navigation")
    public ResponseEntity<?> getPostNavigation(@PathVariable Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "글을 찾을 수 없습니다."));
        }

        Post previousPost = postRepository.findFirstByAuthorAndCreatedAtBeforeOrderByCreatedAtDesc(post.getAuthor(), post.getCreatedAt());
        Post nextPost = postRepository.findFirstByAuthorAndCreatedAtAfterOrderByCreatedAtAsc(post.getAuthor(), post.getCreatedAt());

        Map<String, Object> response = Map.of(
                "previousPost", previousPost != null ? Map.of(
                        "postId", previousPost.getId(),
                        "title", previousPost.getTitle()
                ) : null,
                "nextPost", nextPost != null ? Map.of(
                        "postId", nextPost.getId(),
                        "title", nextPost.getTitle()
                ) : null
        );

        return ResponseEntity.ok(response);
    }


}