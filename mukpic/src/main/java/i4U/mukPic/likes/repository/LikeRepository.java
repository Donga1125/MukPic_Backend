package i4U.mukPic.likes.repository;

import i4U.mukPic.community.entity.Community;
import i4U.mukPic.likes.entity.CommunityLikes;
import i4U.mukPic.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<CommunityLikes, Long>{
    Optional<CommunityLikes> findByUserAndCommunity (User user, Community community);
}
