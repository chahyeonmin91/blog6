package com.example.blog6.service;

import com.example.blog6.model.Comment;
import com.example.blog6.model.Post;
import com.example.blog6.repository.CommentRepository;
import com.example.blog6.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    //api 댓글 작성
    public Comment addComment(Long postId, String content) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return null;
        }
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setContent(content);
        return commentRepository.save(comment);
    }

    //api 댓글 삭제
    public boolean deleteComment(Long postId, Long commentId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return false;
        }
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null || !comment.getPost().getId().equals(postId)) {
            return false;
        }
        commentRepository.delete(comment);
        return true;
    }
}
