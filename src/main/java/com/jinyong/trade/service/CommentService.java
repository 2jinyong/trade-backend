package com.jinyong.trade.service;

import com.jinyong.trade.entity.Comment;
import com.jinyong.trade.entity.Post;
import com.jinyong.trade.entity.User;
import com.jinyong.trade.repository.CommentRepository;
import com.jinyong.trade.repository.PostRepository;
import com.jinyong.trade.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자 자동 생성
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    // 댓글 작성
    public void create(Long postId, String content, String username) {

        // 게시글 존재 여부 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("게시글 없음"));

        // 로그인 사용자 조회
        User user = userRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("유저 없음"));

        // 댓글 엔티티 생성
        Comment comment = new Comment(post, user, content);

        // DB에 INSERT
        commentRepository.save(comment);
    }

    // 게시글별 댓글 조회
    public List<Comment> getComments(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId);
    }

    // 댓글 삭제
    public void delete(Long commentId, String username) {

        // 댓글 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글 없음"));

        // 댓글 작성자와 로그인 사용자 비교
        if (!comment.getUser().getName().equals(username)) {
            throw new RuntimeException("본인 댓글만 삭제 가능");
        }

        // DELETE 실행
        commentRepository.delete(comment);
    }
}
