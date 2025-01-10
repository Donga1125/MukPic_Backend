package i4U.mukPic.openai.repository;

import i4U.mukPic.openai.entity.FoodInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FoodInfoRepository extends JpaRepository<FoodInfo, Long> {
    Optional<FoodInfo> findByFoodName(String foodName); // 메서드명 수정
}
