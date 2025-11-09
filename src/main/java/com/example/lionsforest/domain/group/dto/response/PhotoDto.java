package com.example.lionsforest.domain.group.dto.response;

import com.example.lionsforest.domain.group.GroupPhoto;
import lombok.Getter;

@Getter
public class PhotoDto {
    private Long photoId;
    private String photoUrl;
    private Integer order;

    public PhotoDto(GroupPhoto photo) {
        this.photoId = photo.getId();
        this.photoUrl = photo.getPhoto();
        this.order = photo.getPhoto_order();
    }
}
