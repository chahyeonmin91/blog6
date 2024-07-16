package com.example.blog6.repository;


import com.example.blog6.model.Post;
import com.example.blog6.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUser(User user);
    List<Post> findByOrderByCreatedAtDesc();
    List<Post> findByOrderByLikesDesc();

    //사용자가 읽은 포스트를 조회하는 메서드
    //List<Post> findByReadersContaining(User user);

    // 이전/다음 글 조회 메서드 추가
    Post findFirstByAuthorAndCreatedAtBeforeOrderByCreatedAtDesc(User author, LocalDateTime createdAt);
    Post findFirstByAuthorAndCreatedAtAfterOrderByCreatedAtAsc(User author, LocalDateTime createdAt);

    //사용자와 제목으로 찾는 메서드
    Post findByUserAndTitle(User user, String title);
}
