package i4U.mukPic.community.repository;

import i4U.mukPic.community.entity.Category;
import i4U.mukPic.community.entity.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityRepository extends JpaRepository<Community, Long> {


    // 특정 카테고리에 해당하는 게시글을 조회하는 메서드
    Page<Community> findByCategory(Category category, Pageable pageable);

    // 사용자별 게시글 조회 (최신순)
    Page<Community> findAllByUser_UserKeyOrderByCreatedAtDesc(Long userKey, Pageable pageable);

    // 사용자가 좋아요를 누른 게시글 조회 (최신순)
    Page<Community> findByFeedLikes_User_UserKeyOrderByCreatedAtDesc(Long userKey, Pageable pageable);
}
