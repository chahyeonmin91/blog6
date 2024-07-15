package com.example.blog6.service;

import com.example.blog6.model.Follow;
import com.example.blog6.model.User;
import com.example.blog6.repository.FollowRepository;
import com.example.blog6.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FollowService {

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private UserRepository userRepository;


    //팔로우
    public boolean followUser(String followerUsername, String followeeUsername) {
        User follower = userRepository.findByUsername(followerUsername);
        User followee = userRepository.findByUsername(followeeUsername);

        if (follower == null || followee == null || follower.equals(followee)) {
            return false;
        }

        Follow follow = followRepository.findByFollowerAndFollowee(follower, followee);
        if (follow == null) {
            follow = new Follow();
            follow.setFollower(follower);
            follow.setFollowee(followee);
            followRepository.save(follow);
            return true;
        }

        return false;
    }

    //팔로우 취소
    public boolean unfollowUser(String followerUsername, String followeeUsername) {
        User follower = userRepository.findByUsername(followerUsername);
        User followee = userRepository.findByUsername(followeeUsername);

        if (follower == null || followee == null) {
            return false;
        }

        Follow follow = followRepository.findByFollowerAndFollowee(follower, followee);
        if (follow != null) {
            followRepository.delete(follow);
            return true;
        }

        return false;
    }

    //팔로우 목록 조회
    public List<User> getFollowers(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            List<Follow> follows = followRepository.findByFollower(user);
            return follows.stream()
                    .map(Follow::getFollowee)
                    .collect(Collectors.toList());
        }
        return null;
    }
}