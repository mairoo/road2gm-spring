package kr.co.road2gm.api.domain.auth.repository.jpa;

import kr.co.road2gm.api.domain.auth.domain.SocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialAccountRepository extends JpaRepository<SocialAccount, Long> {
}
