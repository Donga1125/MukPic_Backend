package i4U.mukPic.user.repository;

import i4U.mukPic.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserId(String userId);
    Optional<User> findByEmail(String email);
    Optional<User> findByUserName(String userName);
    Optional<User> findByUserKey(Long userKey);

    boolean existsByEmail(String email); //이 코드들은 단순히 boolean 값만 반환하기때문에 중복여부만 판별할때는 좀 더 성능상 유리함
    boolean existsByUserId(String userId); //위의 코드는 중복된 값이 있다면 그 값을 반환하기 때문에 정보를 조회할때 좀 더 유리
    boolean existsByUserName(String userName);
}
