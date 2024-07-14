package com.example.blog6.service;

import com.example.blog6.model.Post;
import com.example.blog6.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    public Page<Post> getPosts(String sort, Pageable pageable) {
        // 정렬 및 페이징 로직 추가
        return postRepository.findAll(pageable);
    }
}