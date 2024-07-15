package com.example.blog6.repository;


import com.example.blog6.model.Follow;
import com.example.blog6.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    List<Follow> findByFollower(User follower);
    Follow findByFollowerAndFollowee(User follower, User followee);
}
