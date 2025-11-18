package com.example.lionsforest.domain.review.dto.request;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Getter
@NoArgsConstructor
@Schema(description = "후기 생성 요청")
public class ReviewRequestDto {
    @Schema(description = "별점")
    @NotNull
    private Integer score;

    @Schema(description = "후기 내용")
    @NotNull
    private String content;

    @ArraySchema(
            arraySchema = @Schema(description = "업로드할 사진들"),
            schema = @Schema(type = "string", format = "binary")
    )
    private List<MultipartFile> photos;

}
