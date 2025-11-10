package com.example.lionsforest.domain.review.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class ReviewUpdateRequestDto {
    private Integer score;
    private String content;
    private List<Long> deletePhotoIds;
}
