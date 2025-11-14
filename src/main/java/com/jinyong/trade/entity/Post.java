package com.jinyong.trade.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 PK
    private Long id;

    private String userId;   // 작성자 ID (추후 User 엔티티와 관계 맺을 수 있음)

    private String title;    // 제목

    @Column(columnDefinition = "TEXT")
    private String content;  // 내용 (긴 글 저장 가능)

    private int price;       // 가격 (숫자형)

    private String img;      // 이미지 URL
}
