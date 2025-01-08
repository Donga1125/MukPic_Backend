package i4U.mukPic.global.auth.repository;

import java.util.Optional;

import i4U.mukPic.global.auth.entity.Token;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRepository extends CrudRepository<Token, String> {

    Optional<Token> findByAccessToken(String accessToken);
    boolean existsByAccessToken(String accessToken);
}