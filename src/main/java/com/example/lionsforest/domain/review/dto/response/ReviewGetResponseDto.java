package com.example.lionsforest.domain.review.dto.response;

import com.example.lionsforest.domain.review.Review;
import com.example.lionsforest.domain.review.ReviewPhoto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ReviewGetResponseDto {
    private Long id;
    private Long groupId;
    private Long userId;
    private String content;
    private Integer score;
    private LocalDateTime createdAt;

    private List<ReviewPhotoDto> photos;

    public static ReviewGetResponseDto fromEntity(Review review){

        List<ReviewPhotoDto> photos = review.getPhotos().stream()
                .sorted(Comparator.comparing(ReviewPhoto::getPhoto_order))
                .map(ReviewPhotoDto::new)
                .toList();

        return ReviewGetResponseDto.builder()
                .id(review.getId())
                .groupId(review.getGroup().getId())
                .userId(review.getUser().getId())
                .content(review.getContent())
                .score(review.getScore())
                .createdAt(review.getCreatedAt())
                .photos(photos)
                .build();
    }
}
