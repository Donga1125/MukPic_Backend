package i4U.mukPic.notice.dto;

import i4U.mukPic.notice.entity.Notice;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class NoticeResponseDto {
    @Getter
    @NoArgsConstructor
    public static class DetailNotice {
        private Long id;
        private String title;
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public DetailNotice(Notice notice){
            this.id = notice.getId();
            this.title = notice.getTitle();
            this.content = notice.getContent();
            this.createdAt = notice.getCreatedAt();
            this.updatedAt = notice.getUpdatedAt();
        }

    }

    @Getter
    @NoArgsConstructor
    public static class BriefNotice {
        private Long id;
        private String title;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public BriefNotice (Notice notice){
            this.id = notice.getId();
            this.title = notice.getTitle();
            this.createdAt = notice.getCreatedAt();
            this.updatedAt = notice.getUpdatedAt();
        }
    }
}
