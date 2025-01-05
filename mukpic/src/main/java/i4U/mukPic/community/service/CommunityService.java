package i4U.mukPic.community.service;

import i4U.mukPic.community.dto.CommunityRequestDto;
import i4U.mukPic.community.entity.Community;
import i4U.mukPic.community.repository.CommunityRepository;
import i4U.mukPic.global.exception.BusinessLogicException;
import i4U.mukPic.global.exception.ExceptionCode;
import i4U.mukPic.image.service.ImageService;
import i4U.mukPic.user.entity.User;
import i4U.mukPic.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;

    //게시글 생성
    public Community createCommunityFeed(CommunityRequestDto.Post postDto) {
        User user = userRepository.findByUserKey(postDto.getUserKey()).orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.USER_NOT_FOUND)
        );

        Community community = Community.createFeed(postDto, user);

        return communityRepository.save(community);
    }

    //게시글 전체 조회
    public List<Community> getCommunityPosts(Short category, Short sort) {
        Sort sorting = createSorting(sort);

        // 카테고리 필터링
        if (category == 1) {
            // 카테고리가 전체일 경우, 모든 게시글 반환
            return communityRepository.findAll(sorting);
        } else {
            // 특정 카테고리의 게시글 반환
            String categoryName = mapCategory(category);
            return communityRepository.findByCategory(categoryName, sorting);
        }
    }

    // 정렬 기준 설정
    private Sort createSorting(Short sort) {
        if (sort == 1) {
            return Sort.by(Sort.Direction.DESC, "createdAt"); // 최신순
        } else if (sort == 2) {
            return Sort.by(Sort.Direction.DESC, "likes"); // 좋아요 순
        } else {
            throw new IllegalArgumentException("Invalid sort type: " + sort);
        }
    }

    // 카테고리 매핑
    private String mapCategory(Short category) {
        return switch (category) {
            case 2 -> "밥";
            case 3 -> "면";
            case 4 -> "빵";
            default -> throw new IllegalArgumentException("Invalid category: " + category);
        };
    }


    //게시글 상세 조회
    public Community findCommunityById (Long communityKey){
        Community community = checkFeed(communityKey);
        return community;
    }

    //게시글 수정
    public Community updateFeed(Long communityKey, CommunityRequestDto.Patch patchDto) {
        Community existingFeed = checkFeed(communityKey);

        if (patchDto.getTitle() != null) {
            existingFeed.updateTitle(patchDto.getTitle());
        }
        if (patchDto.getContent() != null) {
            existingFeed.updateContent(patchDto.getContent());
        }
        if (patchDto.getImageUrl()!= null) {
            imageService.updateReferenceIdAndType(existingFeed.getCommunityKey(), (short)2, patchDto.getImageUrl());
        }
        if (patchDto.getCommunityCategory() != null) {
            existingFeed.updateCategory(patchDto.getCommunityCategory());
        }

        return existingFeed;
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

}

