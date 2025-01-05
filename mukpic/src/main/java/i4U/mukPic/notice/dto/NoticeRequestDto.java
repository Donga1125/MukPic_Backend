package i4U.mukPic.notice.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotBlank;

public class NoticeRequestDto {
    @Getter
    @NoArgsConstructor
    public static class Post {

        @NotBlank
        @Size(max = 50, message = "제목 글자수 제한 초과")
        private String title;
        @NotBlank
        private String content;

    }

    @Getter
    @NoArgsConstructor
    public static class Patch {

        @Size(max = 50, message = "제목 글자수 제한 초과")
        private String title;
        private String content;

    }
}
