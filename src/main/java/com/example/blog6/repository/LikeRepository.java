package com.example.blog6.repository;

import com.example.blog6.model.Like;
import com.example.blog6.model.Post;
import com.example.blog6.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserAndPost(User user, Post post);

    //사용자가 좋아요한 포스트를 조회하는 메서드
    List<Like> findByUser(User user);
}