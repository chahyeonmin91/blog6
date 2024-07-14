package com.example.blog6.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String excerpt;
    private String content;
    private String authorUsername;
    private String authorProfileImage;
    private int likes;
    private String tags;
    private Boolean publishStatus;


}