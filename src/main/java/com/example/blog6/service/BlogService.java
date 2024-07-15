package com.example.blog6.service;

import com.example.blog6.model.User;
import com.example.blog6.repository.PostRepository;
import com.example.blog6.repository.SeriesRepository;
import com.example.blog6.repository.TagRepository;
import com.example.blog6.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BlogService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private SeriesRepository seriesRepository;

    public Map<String, Object> getUserBlogPage(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            return null;
        }

        List<Map<String, Object>> tags = tagRepository.findByUser(user).stream().map(tag -> Map.of(
                "tag", (Object) tag.getName(),
                "count", (Object) tag.getCount()
        )).collect(Collectors.toList());

        List<Map<String, Object>> posts = postRepository.findByUser(user).stream().map(post -> Map.of(
                "postId", (Object) post.getId().toString(),
                "title", (Object) post.getTitle(),
                "excerpt", (Object) post.getExcerpt(),
                "createdAt", (Object) post.getCreatedAt().toString(),
                "series", (Object) (post.getSeries() != null ? post.getSeries().getTitle() : null)
        )).collect(Collectors.toList());

        List<Map<String, Object>> seriesList = seriesRepository.findByUser(user).stream().map(series -> Map.of(
                "seriesId", (Object) series.getId().toString(),
                "title", (Object) series.getTitle(),
                "posts", series.getPosts().stream().map(seriesPost -> Map.of(
                        "postId", (Object) seriesPost.getId().toString(),
                        "title", (Object) seriesPost.getTitle()
                )).collect(Collectors.toList())
        )).collect(Collectors.toList());

        return Map.of(
                "tags", tags,
                "posts", posts,
                "series", seriesList,
                "introduction", user.getIntroduction()
        );
    }
}