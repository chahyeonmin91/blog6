package com.example.blog6.repository;

import com.example.blog6.model.Tag;
import com.example.blog6.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    List<Tag> findByUser(User user);
}