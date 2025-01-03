package i4U.mukPic.community.repository;

import i4U.mukPic.community.entity.Community;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityRepository extends JpaRepository<Community, Long> {

    //좋아요 순 내림차순, 동일 좋아요 수에서는 최신 순 정렬
    List<Community> findAllByOrderByLikeCountDescCreateAtDesc();

    //최신순으로 정렬
    List<Community> findByCommunityOrderByCreateAtDesc();

    // 카테고리에 따른 게시글 조회
    List<Community> findByCategory(String category, Sort sort);


}
