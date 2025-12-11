package com.jinyong.trade.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikeDto {
    private boolean liked;      // 현재 좋아요 상태
    private long likeCount;     // 총 좋아요 개수
}
