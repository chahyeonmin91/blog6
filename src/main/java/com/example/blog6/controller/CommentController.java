package com.example.blog6.controller;


import com.example.blog6.model.Comment;
import com.example.blog6.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class CommentController {

    @Autowired
    private CommentService commentService;

    //api 댓글 작성
    @PostMapping("/{postId}/comments")
    public ResponseEntity<?> addComment(@PathVariable Long postId, @RequestBody Map<String, String> request) {
        String content = request.get("content");
        Comment comment = commentService.addComment(postId, content);
        if (comment != null) {
            return ResponseEntity.ok(Map.of(
                    "message", "댓글 작성이 완료되었습니다.",
                    "commentId", comment.getId().toString()
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "잘못된 요청입니다."));
        }
    }

    // 답글 작성
    @PostMapping("/{postId}/comments/{parentCommentId}/replies")
    public ResponseEntity<?> addReply(@PathVariable Long postId, @PathVariable Long parentCommentId, @RequestBody Map<String, String> request) {
        String content = request.get("content");
        Comment reply = commentService.addReply(postId, parentCommentId, content);
        if (reply != null) {
            return ResponseEntity.ok(Map.of(
                    "message", "답글 작성이 완료되었습니다.",
                    "replyId", reply.getId().toString()
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "잘못된 요청입니다."));
        }
    }

    //api 댓글 삭제
    @DeleteMapping("/{postId}/comments/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long postId, @PathVariable Long commentId) {
        boolean isDeleted = commentService.deleteComment(postId, commentId);
        if (isDeleted) {
            return ResponseEntity.ok(Map.of("message", "댓글 삭제가 완료되었습니다."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "잘못된 요청입니다."));
        }
    }
}
