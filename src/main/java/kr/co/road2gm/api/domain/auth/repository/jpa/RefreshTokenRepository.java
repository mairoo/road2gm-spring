package kr.co.road2gm.api.domain.auth.repository.jpa;

import kr.co.road2gm.api.domain.auth.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying(clearAutomatically = true)  // 영속성 컨텍스트 자동 초기화
    @Query("DELETE FROM RefreshToken rt WHERE rt.email = :email")
    int deleteAllByEmail(@Param("email") String email);
}
