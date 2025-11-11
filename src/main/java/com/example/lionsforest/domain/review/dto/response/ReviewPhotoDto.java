package com.example.lionsforest.domain.review.dto.response;

import com.example.lionsforest.domain.review.ReviewPhoto;
import lombok.Getter;

@Getter
public class ReviewPhotoDto {
    private final String photoUrl;
    private final Integer order;

    public ReviewPhotoDto(ReviewPhoto photo) {
        this.photoUrl = photo.getPhoto();
        this.order = photo.getPhoto_order();
    }
}
