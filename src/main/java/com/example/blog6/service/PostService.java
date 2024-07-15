package com.example.blog6.service;

import com.example.blog6.model.Post;
import com.example.blog6.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

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
        if (post != null) {
            postRepository.delete(post);
            return true;
        }
        return false;
    }

    //글 목록 조회
    public List<Post> getAllPostsIncludingDrafts() {
        return postRepository.findAll();
    }
}