package i4U.mukPic.likes.controller;

import i4U.mukPic.global.jwt.security.JwtTokenProvider;
import i4U.mukPic.likes.service.LikeService;
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
    private final JwtTokenProvider jwtTokenProvider;

    //게시글 좋아요
    @PostMapping("/community/{community-key}/likes")
    public ResponseEntity createFeedLikes (@PathVariable("community-key") Long communityKey,
                                           HttpServletRequest request){

        String accessToken = jwtTokenProvider.extractAccessToken(request)
                .orElseThrow(() -> new RuntimeException("Access Token이 없습니다."));
        // 새 Access Token으로 사용자 ID 추출
        String userKey = jwtTokenProvider.extractSubject(accessToken)
                .orElseThrow(() -> new RuntimeException("토큰에서 사용자 ID 정보를 가져올 수 없습니다."));
        likeService.createFeedLike(userKey, communityKey);
        return ResponseEntity.ok().build();
    }

    //게시글 좋아요 취소
    @DeleteMapping("/community/{community-id}/likes")
    public ResponseEntity deleteFeedLikes (@PathVariable("community-id") Long feedId){

        likeService.deleteFeedLike(userId, feedId);
        return ResponseEntity.ok().build();
    }
}
