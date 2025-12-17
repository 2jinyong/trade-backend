package com.jinyong.trade.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    // 여러 Comment가 하나의 Post에 속함 (N:1 관계)
    // LAZY: 실제로 필요할 때만 Post 조회 (성능 최적화)
    @JoinColumn(name = "post_id")
    // comment 테이블에 post_id 컬럼 생성
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    // 여러 Comment가 하나의 User에 의해 작성됨
    @JoinColumn(name = "user_id")
    // comment 테이블에 user_id 컬럼 생성
    private User user;

    @Column(nullable = false, length = 500)
    // NOT NULL + 최대 500자 제한
    private String content;

    // 댓글 생성 시 사용하는 생성자
    // id는 자동 생성이므로 받지 않음
    public Comment(Post post, User user, String content) {
        this.post = post;
        this.user = user;
        this.content = content;
    }
}
