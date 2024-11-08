package kr.co.road2gm.api.domain.auth.repository.jpa;

import kr.co.road2gm.api.domain.auth.domain.SocialAccountState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SocialAccountStateRepository extends JpaRepository<SocialAccountState, Long> {
    Optional<SocialAccountState> findByState(String state);

    /*
    - JPA는 기본적으로 엔티티의 생명주기를 관리하기 위해 각 엔티티를 개별적으로 처리
    - deleteBy* 메서드는 내부적으로 먼저 select로 엔티티를 조회한 후, 각 엔티티에 대해 개별 delete를 실행
    int deleteByCreatedBefore(LocalDateTime dateTime);
    */

    // 강제 벌크 삭제
    @Modifying(clearAutomatically = true)  // 영속성 컨텍스트 자동 초기화
    @Query("DELETE FROM SocialAccountState e WHERE e.created < :dateTime")
    int deleteAllCreatedBefore(@Param("dateTime") LocalDateTime dateTime);
}
