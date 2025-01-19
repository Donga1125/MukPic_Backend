package i4U.mukPic.community.controller;

import i4U.mukPic.community.dto.CommunityRequestDto;
import i4U.mukPic.community.dto.CommunityResponseDto;
import i4U.mukPic.community.entity.Category;
import i4U.mukPic.community.service.CommunityService;
import i4U.mukPic.user.entity.User;
import i4U.mukPic.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
@Validated
public class CommunityController {
    private final CommunityService communityService;
    private final UserService userService;

    //커뮤니티 글 등록
    @PostMapping
    public ResponseEntity postCommunityFeed (@Valid @RequestBody CommunityRequestDto.Post postDto,
                                             HttpServletRequest request){
        User user = userService.getUserFromRequest(request);
        CommunityResponseDto responseDto = communityService.createCommunityFeed(postDto, user);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    // 커뮤니티 글 조회 (무한 스크롤 적용, 카테고리 필터링 포함)
    @GetMapping
    public ResponseEntity<Page<CommunityResponseDto>> getCommunityFeeds(
            @RequestParam(defaultValue = "ALL") Category category,
            @RequestParam(defaultValue = "latest") String sortBy,
            HttpServletRequest request, Pageable pageable) {

        User user = userService.getUserFromRequest(request);
        Page<CommunityResponseDto> responseDtos;

        if ("ALL".equalsIgnoreCase(String.valueOf(category))) {
            responseDtos = communityService.getAllCommunityFeeds(sortBy, pageable, user);
        } else {
            responseDtos = communityService.getCommunityFeedsByCategory(category, sortBy, pageable, user);
        }

        return new ResponseEntity<>(responseDtos, HttpStatus.OK);
    }

    //내가 쓴 게시글 조회
    @GetMapping("/myCommunities")
    public ResponseEntity getMyCommunityFeed (HttpServletRequest request, Pageable pageable) {

        User user = userService.getUserFromRequest(request);
        Page<CommunityResponseDto> myCommunities = communityService.findMyCommunityFeeds(user, pageable);

        return new ResponseEntity<>(myCommunities, HttpStatus.OK);

    }

    //내가 좋아요 누른 게시글 조회
    @GetMapping("/likedCommunities")
    public ResponseEntity getLikedCommunities(HttpServletRequest request, Pageable pageable) {
        User user = userService.getUserFromRequest(request);
        Page<CommunityResponseDto> likedCommunities = communityService.findLikedCommunities(user, pageable);

        return new ResponseEntity<>(likedCommunities, HttpStatus.OK);
    }

    //커뮤니티 글 상세 조회
    @GetMapping("{community-key}")
    public ResponseEntity getCommunityFeed (@PathVariable ("community-key") Long communityKey, HttpServletRequest request){
        User user = userService.getUserFromRequest(request);
        CommunityResponseDto responseDto = communityService.findCommunityById(communityKey, user);

        return new ResponseEntity(responseDto, HttpStatus.OK);
    }


    //커뮤니티 글 수정
    @PatchMapping("{community-key}")
    public ResponseEntity patchCommunityFeed (@PathVariable ("community-key") Long communityKey, @RequestBody CommunityRequestDto.Patch patchDto,
                                              HttpServletRequest request){
        User user = userService.getUserFromRequest(request);
        CommunityResponseDto responseDto = communityService.updateFeed(communityKey, patchDto,user);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    //커뮤니티 글 삭제
    @DeleteMapping("{community-key}")
    public ResponseEntity deleteCommunityFeed (@PathVariable ("community-key")Long communityKey){
        communityService.deleteCommunityFeed(communityKey);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }


}
