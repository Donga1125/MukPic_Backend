package i4U.mukPic.likes.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import i4U.mukPic.community.entity.Community;
import i4U.mukPic.global.config.Timestamped;
import i4U.mukPic.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity(name = "community_likes")
public class CommunityLikes extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long communityKey;

    @ManyToOne
    @JoinColumn(name = "user_key")
    @JsonBackReference
    private User user;

    @ManyToOne
    @JoinColumn(name = "community_id")
    @JsonBackReference
    private Community community;
}
