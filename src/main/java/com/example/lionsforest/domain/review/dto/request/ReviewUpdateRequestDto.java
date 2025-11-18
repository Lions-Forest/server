package com.example.lionsforest.domain.review.dto.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Getter
@NoArgsConstructor
public class ReviewUpdateRequestDto {
    @Schema(description = "별점")
    private Integer score;

    @Schema(description = "후기 내용")
    private String content;

    @Schema(description = "삭제할 사진의 아이디 리스트")
    private List<Long> deletePhotoIds;

    @ArraySchema(
            arraySchema = @Schema(description = "추가할 사진들"),
            schema = @Schema(type = "string", format = "binary")
    )
    private List<MultipartFile> addPhotos;
}
