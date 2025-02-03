package i4U.mukPic.community.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import i4U.mukPic.community.dto.CommunityRequestDto;
import i4U.mukPic.global.config.Timestamped;
import i4U.mukPic.image.entity.Image;
import i4U.mukPic.likes.entity.CommunityLikes;
import i4U.mukPic.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "community")
public class Community extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long communityKey;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false)
    private String content;

    @Formula("(SELECT COUNT(*) FROM community_likes cl WHERE cl.community_id = community_key)")
    private int likeCount;

    @Enumerated(EnumType.STRING)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "user_key")
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "community", cascade = {CascadeType.PERSIST,CascadeType.REMOVE})
    private List<CommunityLikes> feedLikes  = new ArrayList<>();

    @OneToMany(mappedBy = "referenceId", fetch = FetchType.LAZY)
    private List<Image> imageUrl = new ArrayList<>();

    public void updateTitle (String title){
        this.title= title;
    }
    public void updateContent (String content){
        this.content = content;
    }

    public void updateCategory (Category communityCategory) { this.category = communityCategory; }
    public void updateLikeCount(int num) {this.likeCount = likeCount + num; }
    public static Community createFeed (CommunityRequestDto.Post postDto, User user){
        Community community = new Community();
        community.title = postDto.getTitle();
        community.content = postDto.getContent();
        community.category= Category.valueOf(postDto.getCategory().toUpperCase());
        community.user = user;
        return community;
    }

}
