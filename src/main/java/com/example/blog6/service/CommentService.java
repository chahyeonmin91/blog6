package com.example.blog6.service;

import com.example.blog6.model.Comment;
import com.example.blog6.model.Post;
import com.example.blog6.repository.CommentRepository;
import com.example.blog6.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

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
    // 답글 작성
    public Comment addReply(Long postId, Long parentCommentId, String content) {
        Post post = postRepository.findById(postId).orElse(null);
        Comment parentComment = commentRepository.findById(parentCommentId).orElse(null);
        if (post == null || parentComment == null) {
            return null;
        }
        Comment reply = new Comment();
        reply.setPost(post);
        reply.setParentComment(parentComment);
        reply.setContent(content);
        return commentRepository.save(reply);
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
