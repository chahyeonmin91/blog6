package com.example.blog6.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Entity
@Getter
@Setter

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String name;
    private String email;
    private String profileImage;
    private String blogTitle;
    private Boolean emailNotificationComment;
    private Boolean emailNotificationUpdate;
    private String introduction;


    @OneToMany(mappedBy = "author")
    private List<Post> posts;

    @OneToMany(mappedBy = "user")
    private List<Tag> tags;

    @OneToMany(mappedBy = "user")
    private List<Series> series;

    @PrePersist
    @PreUpdate
    private void setDefaultBlogTitle() {
        if (this.blogTitle == null || this.blogTitle.isEmpty()) {
            this.blogTitle = this.username;
        }
    }
}
