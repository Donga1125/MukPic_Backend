package i4U.mukPic.likes.controller;

import i4U.mukPic.global.jwt.security.JwtTokenProvider;
import i4U.mukPic.likes.service.LikeService;
import i4U.mukPic.user.entity.User;
import i4U.mukPic.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
@Validated
public class LikeController {

    private final LikeService likeService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    //게시글 좋아요
    @PostMapping("/community/{community-key}/likes")
    public ResponseEntity createFeedLikes (@PathVariable("community-key") Long communityKey,
                                           HttpServletRequest request){


        User user = userService.getUserFromRequest(request);
        likeService.createFeedLike(user.getUserKey(), communityKey);
        return ResponseEntity.ok().build();
    }

    //게시글 좋아요 취소
    @DeleteMapping("/community/{community-id}/likes")
    public ResponseEntity deleteFeedLikes (@PathVariable("community-id") Long feedId,
                                           HttpServletRequest request){

        User user = userService.getUserFromRequest(request);
        likeService.deleteFeedLike(user.getUserKey(), feedId);
        return ResponseEntity.ok().build();
    }
}
