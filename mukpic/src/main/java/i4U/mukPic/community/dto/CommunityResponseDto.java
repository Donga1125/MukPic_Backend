package i4U.mukPic.community.dto;

import i4U.mukPic.community.entity.Community;
import i4U.mukPic.image.entity.Image;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CommunityResponseDto {
    private Long communityKey;
    private String title;
    private String content;
    private List<Image> imageUrls;

    public CommunityResponseDto(Community community, List<Image> imageUrls){
        this.communityKey = community.getCommunityKey();
        this.title = community.getTitle();
        this.content= community.getContent();
        this.imageUrls = imageUrls;
    }


}
