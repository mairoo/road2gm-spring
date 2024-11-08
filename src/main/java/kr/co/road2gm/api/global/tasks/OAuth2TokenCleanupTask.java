package kr.co.road2gm.api.global.tasks;

import kr.co.road2gm.api.domain.auth.repository.jpa.OAuth2TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2TokenCleanupTask {
    private final OAuth2TokenRepository OAuth2TokenRepository;

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("Starting cleanup of expired auth tokens...");

        int deletedCount = OAuth2TokenRepository.deleteAllCreatedBefore(LocalDateTime.now().minusMinutes(1));

        log.info("Cleanup completed. Deleted {} expired tokens", deletedCount);
    }
}
