package i4U.mukPic.likes.service;

import i4U.mukPic.community.entity.Community;
import i4U.mukPic.community.service.CommunityService;
import i4U.mukPic.global.exception.BusinessLogicException;
import i4U.mukPic.global.exception.ExceptionCode;
import i4U.mukPic.likes.entity.CommunityLikes;
import i4U.mukPic.likes.repository.LikeRepository;
import i4U.mukPic.user.entity.User;
import i4U.mukPic.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
    private final UserService userService;
    private final CommunityService communityService;
    private final LikeRepository likeRepository;


    //게시글 좋아요
    public void createFeedLike (Long userKey, Long communityKey) {
        User user = userService.getUserInfo(userKey);
        Community community = communityService.checkFeed(communityKey);
        Optional<CommunityLikes> feedLikesOptional = likeRepository.findByUserAndCommunity(user, community);

        if (feedLikesOptional.isPresent()){
            throw new BusinessLogicException(ExceptionCode.DUPLICATE_ERROR);
        }else {
            CommunityLikes communityLikes = CommunityLikes.builder()
                    .user(user)
                    .community(community)
                    .build();

            community.updateLikeCount(1);
            likeRepository.save(communityLikes);

        }
    }

    //게시글 좋아요 취소
    public void deleteFeedLike (Long userKey, Long communityKey){
        User user = userService.getUserInfo(userKey);
        Community community = communityService.checkFeed(communityKey);
        CommunityLikes communityLikes = likeRepository.findByUserAndCommunity(user, community).orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.NOT_FOUND));

        community.updateLikeCount(-1);
        likeRepository.deleteById(communityLikes.getCommunityKey());
    }
}
