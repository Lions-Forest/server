package com.example.lionsforest.domain.group.dto.response;

import com.example.lionsforest.domain.group.GroupPhoto;

import lombok.Getter;

@Getter
public class GroupPhotoDto {
    private final String photoUrl;
    private final Integer order;

    public GroupPhotoDto(GroupPhoto photo) {
        this.photoUrl = photo.getPhoto();
        this.order = photo.getPhoto_order();
    }
}
