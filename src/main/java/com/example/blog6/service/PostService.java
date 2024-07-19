package com.example.blog6.service;

import com.example.blog6.model.Like;
import com.example.blog6.model.Post;
import com.example.blog6.model.Series;
import com.example.blog6.model.User;
import com.example.blog6.repository.LikeRepository;
import com.example.blog6.repository.PostRepository;
import com.example.blog6.repository.SeriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final SeriesRepository seriesRepository;

    public Page<Post> getPosts(String sort, Pageable pageable) {
        // 정렬 및 페이징 로직 추가
        return postRepository.findAll(pageable);
    }


    //관리자 모든 글 조회
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    //관리자 권한 삭제
    public boolean deletePost(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post != null && !post.getPublishStatus()) {
            postRepository.delete(post);
            return true;
        }
        return false;
    }


    //글 목록 조회
    public List<Post> getAllPostsIncludingDrafts() {
        return postRepository.findAll();
    }

    //최신글 인기글
    public List<Post> getPosts(String sort) {
        if ("latest".equalsIgnoreCase(sort)) {
            return postRepository.findByOrderByCreatedAtDesc();
        } else if ("popular".equalsIgnoreCase(sort)) {
            return postRepository.findByOrderByLikesDesc();
        } else {
            throw new IllegalArgumentException("Invalid sort parameter");
        }
    }
    // 좋아요한 글들 조회
    public List<Post> getLikedPosts(User user) {
        List<Like> likes = likeRepository.findByUser(user);
        return likes.stream().map(Like::getPost).collect(Collectors.toList());
    }

    // 사용자가 읽은 글들 조회
    public List<Post> getReadPosts(User user) {
         return null;
    }


    //포스트 미리보기 이미지 등록 및 시리즈 추가 기능
    public boolean savePost(Post post, MultipartFile previewImage, Long seriesId) throws IOException {
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
        return true;
    }
}