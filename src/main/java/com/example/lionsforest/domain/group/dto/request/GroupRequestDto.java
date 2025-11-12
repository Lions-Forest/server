package com.example.lionsforest.domain.group.dto.request;

import com.example.lionsforest.domain.group.GroupCategory;
import com.example.lionsforest.domain.group.GroupState;
import com.example.lionsforest.domain.user.User;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import com.example.lionsforest.domain.group.Group;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Getter
@NoArgsConstructor
@Schema(description = "모임 생성 요청")
public class GroupRequestDto {
    @Schema(description = "모임 제목")
    @NotBlank
    private String title;

    @Schema(description = "모임 카테고리")
    @NotNull
    private GroupCategory category;

    @Schema(description = "모임 정원")
    @NotNull
    private Integer capacity;

    @Schema(description = "모임 일시")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @NotNull
    private LocalDateTime meetingAt;

    @Schema(description = "모임 장소")
    @NotBlank
    private String location;

    @ArraySchema(
            arraySchema = @Schema(description = "업로드할 사진들"),
            schema = @Schema(type = "string", format = "binary")
    )
    private List<MultipartFile> photos;

    public Group toEntity(User leader){
        return Group.builder()
                .title(this.title)
                .category(this.category)
                .capacity(this.capacity)
                .meetingAt(this.meetingAt)
                .location(this.location)
                .state(GroupState.OPEN) // 기본 설정 : 모집중
                .leader(leader)
                .build();
    }

}
