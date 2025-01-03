package i4U.mukPic.community.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class CommunityRequestDto {

    @Getter
    @NoArgsConstructor
    public class Post {
        @NotBlank
        private Long userKey;

        @NotBlank
        private String title;

        @NotBlank
        private String content;

        // 이미지 URL 리스트의 크기를 5개로 제한
        @Size(max = 5, message = "이미지는 최대 5개까지 업로드할 수 있습니다.")
        private List<String> imageUrl;

        @NotBlank
        private Short communityCategory;
    }

    @Getter
    @NoArgsConstructor
    public class Patch {
        @NotBlank
        private Long userKey;

        private String title;

        private String content;

        @NotBlank
        private Short communityCategory;


        // 이미지 URL 리스트의 크기를 5개로 제한
        @Size(max = 5, message = "이미지는 최대 5개까지 업로드할 수 있습니다.")
        private List<String> imageUrl;

    }
}
