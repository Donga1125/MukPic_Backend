package i4U.mukPic.community.dto;

import i4U.mukPic.community.entity.Community;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class CommunityResponseDto {
    private Long communityKey;
    private String title;
    private String content;
    private List<String> imageUrls;
    private String category;
    private int likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String userName;
    private String profileImage;
    private boolean isLiked;

    public CommunityResponseDto(Community community, List<String> imageUrls, boolean isLiked){
        this.communityKey = community.getCommunityKey();
        this.title = community.getTitle();
        this.content= community.getContent();
        this.imageUrls = imageUrls;
        this.category= community.getCategory().toString();
        this.likeCount = community.getLikeCount();
        this.createdAt = community.getCreatedAt();
        this.updatedAt = community.getUpdatedAt();
        this.userName = community.getUser().getUserName();
        this.profileImage = community.getUser().getImage();
        this.isLiked = isLiked;
    }

}
