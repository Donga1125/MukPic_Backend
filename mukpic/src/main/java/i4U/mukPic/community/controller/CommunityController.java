package i4U.mukPic.community.controller;

import i4U.mukPic.community.dto.CommunityRequestDto;
import i4U.mukPic.community.dto.CommunityResponseDto;
import i4U.mukPic.community.entity.Community;
import i4U.mukPic.community.service.CommunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
@Validated
public class CommunityController {
    private final CommunityService communityService;

    //커뮤니티 글 등록
    @PostMapping
    public ResponseEntity postCommunityFeed (@Valid @RequestBody CommunityRequestDto.Post postDto){
        Community community = communityService.createCommunityFeed(postDto);
        CommunityResponseDto responseDto = new CommunityResponseDto(community, community.getImages());

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    //커뮤니티 글 전체 조회
    @GetMapping
    public ResponseEntity<List<Community>> getCommunityFeedsList(
            @RequestParam(defaultValue = "1") Short category, // 기본값: 전체
            @RequestParam(defaultValue = "1") Short sort) {   // 기본값: 최신순

        List<Community> posts = communityService.getCommunityPosts(category, sort);
        return ResponseEntity.ok(posts);
    }

    //커뮤니티 글 상세 조회
    @GetMapping("{community-key}")
    public ResponseEntity getCommunityFeed (@PathVariable ("community-key") Long communityKey){
        Community community = communityService.findCommunityById(communityKey);
        CommunityResponseDto communityResponseDto = new CommunityResponseDto(community, community.getImages());

        return new ResponseEntity(communityResponseDto, HttpStatus.OK);
    }


    //커뮤니티 글 수정
    @PatchMapping("{community-key}")
    public ResponseEntity patchCommunityFeed (@PathVariable ("community-key") Long communityKey, @RequestBody CommunityRequestDto.Patch patchDto){
        Community community = communityService.updateFeed(communityKey, patchDto);
        CommunityResponseDto communityResponseDto = new CommunityResponseDto(community, community.getImages());

        return new ResponseEntity<>(communityResponseDto, HttpStatus.OK);
    }

    //커뮤니티 글 삭제
    @DeleteMapping("{community-key}")
    public ResponseEntity deleteCommunityFeed (@PathVariable ("community-key")Long communityKey ){
        communityService.deleteCommunityFeed(communityKey);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }



}
