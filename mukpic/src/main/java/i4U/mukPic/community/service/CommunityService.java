package i4U.mukPic.community.service;

import i4U.mukPic.community.dto.CommunityRequestDto;
import i4U.mukPic.community.dto.CommunityResponseDto;
import i4U.mukPic.community.entity.Category;
import i4U.mukPic.community.entity.Community;
import i4U.mukPic.community.repository.CommunityRepository;
import i4U.mukPic.global.exception.BusinessLogicException;
import i4U.mukPic.global.exception.ExceptionCode;
import i4U.mukPic.image.entity.ImageType;
import i4U.mukPic.image.service.ImageService;
import i4U.mukPic.user.entity.User;
import i4U.mukPic.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;

    //게시글 생성
    public CommunityResponseDto createCommunityFeed(CommunityRequestDto.Post postDto, User user) {

        Community community = Community.createFeed(postDto, user);
        communityRepository.save(community);
        // 이미지 URL이 null일 경우 빈 리스트로 초기화
        List<String> imageUrls = postDto.getImageUrl()!= null ? postDto.getImageUrl(): new ArrayList<>();

        imageService.updateReferenceIdAndType(community.getCommunityKey(), ImageType.COMMUNITY, imageUrls);
        return createCommunityResponseDto(community);
    }

    @Transactional(readOnly = true)
    public Page<CommunityResponseDto> getAllCommunityFeeds(String sortBy, Pageable pageable) {
        Page<Community> communities;
        Sort sort = "likes".equalsIgnoreCase(sortBy)
                ? Sort.by(Sort.Direction.DESC, "likeCount")
                : Sort.by(Sort.Direction.DESC, "createdAt");

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        communities = communityRepository.findAll(sortedPageable);

        return communities.map(community -> createCommunityResponseDto(community));
    }

    @Transactional(readOnly = true)
    public Page<CommunityResponseDto> getCommunityFeedsByCategory(Category category, String sortBy, Pageable pageable) {
        Page<Community> communities;
        Sort sort = "likes".equalsIgnoreCase(sortBy)
                ? Sort.by(Sort.Direction.DESC, "likeCount")
                : Sort.by(Sort.Direction.DESC, "createdAt");

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        communities = communityRepository.findByCategory(category, sortedPageable);

        return communities.map(community -> createCommunityResponseDto(community));
    }



    //게시글 상세 조회
    public CommunityResponseDto findCommunityById (Long communityKey){
        Community community = checkFeed(communityKey);
        return createCommunityResponseDto(community);
    }

    //내가 쓴 게시글 목록 조회
    public Page<CommunityResponseDto> findMyCommunityFeeds(Long userKey, Pageable pageable) {
        return communityRepository.findAllByUser_UserKeyOrderByCreatedAtDesc(userKey, pageable)
                .map(community -> createCommunityResponseDto(community));
    }

    // 내가 좋아요를 누른 게시글 조회
    public Page<CommunityResponseDto> findLikedCommunities(Long userKey, Pageable pageable) {
        return communityRepository.findByFeedLikes_User_UserKeyOrderByCreatedAtDesc(userKey, pageable)
                .map(community -> createCommunityResponseDto(community));
    }

    //게시글 수정
    public CommunityResponseDto updateFeed(Long communityKey, CommunityRequestDto.Patch patchDto) {
        Community existingFeed = checkFeed(communityKey);

        if (patchDto.getTitle() != null) {
            existingFeed.updateTitle(patchDto.getTitle());
        }
        if (patchDto.getContent() != null) {
            existingFeed.updateContent(patchDto.getContent());
        }
        if (patchDto.getImageUrl()!= null) {
            imageService.updateReferenceIdAndType(existingFeed.getCommunityKey(), ImageType.COMMUNITY, patchDto.getImageUrl());
        }
        if (patchDto.getCategory() != null) {
            existingFeed.updateCategory(Category.valueOf(patchDto.getCategory().toUpperCase()));
        }

        return createCommunityResponseDto(existingFeed);
    }

    //게시글 삭제
    @Transactional
    public void deleteCommunityFeed(Long communityKey){
        checkFeed(communityKey);
        communityRepository.deleteById(communityKey);
    }


    //게시글이 존재하는지 확인
    public Community checkFeed (Long communityKey){
        Community community = communityRepository.findById(communityKey).orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND));
        return community;
    }

    public CommunityResponseDto createCommunityResponseDto (Community community) {
        List<String> imageUrls = imageService.getImagesByReferenceIdAndType(community.getCommunityKey(), ImageType.COMMUNITY);

        return new CommunityResponseDto(community, imageUrls);
    }

}

