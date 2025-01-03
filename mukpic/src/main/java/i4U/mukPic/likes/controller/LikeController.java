package i4U.mukPic.likes.controller;

import i4U.mukPic.likes.service.LikeService;
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

//    //게시글 좋아요
//    @PostMapping("/community/{community-id}/likes")
//    public ResponseEntity createFeedLikes (@PathVariable("community-id") Long communityId){
//
//        likeService.createFeedLike(userId, feedId);
//        return ResponseEntity.ok().build();
//    }

//    //게시글 좋아요 취소
//    @DeleteMapping("/community/{community-id}/likes")
//    public ResponseEntity deleteFeedLikes (@PathVariable("community-id") Long feedId){
//
//        likeService.deleteFeedLike(userId, feedId);
//        return ResponseEntity.ok().build();
//    }
}
