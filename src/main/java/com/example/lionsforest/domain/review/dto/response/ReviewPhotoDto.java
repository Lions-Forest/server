package com.example.lionsforest.domain.review.dto.response;

import com.example.lionsforest.domain.review.ReviewPhoto;
import lombok.Getter;

@Getter
public class ReviewPhotoDto {
    private final Long id;
    private final String photoUrl;
    private final Integer order;

    public ReviewPhotoDto(ReviewPhoto photo) {
        this.id = photo.getId();
        this.photoUrl = photo.getPhoto();
        this.order = photo.getPhoto_order();
    }
}
