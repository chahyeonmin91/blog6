package com.example.blog6.service;

import com.example.blog6.model.Like;
import com.example.blog6.model.Post;
import com.example.blog6.model.User;
import com.example.blog6.repository.LikeRepository;
import com.example.blog6.repository.PostRepository;
import com.example.blog6.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {


    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public boolean addLike(Long postId, String username) {
        User user = userRepository.findByUsername(username);
        Post post = postRepository.findById(postId).orElse(null);

        if (user == null || post == null || likeRepository.existsByUserAndPost(user, post)) {
            return false;
        }

        Like like = new Like();
        like.setUser(user);
        like.setPost(post);
        likeRepository.save(like);

        return true;
    }
}