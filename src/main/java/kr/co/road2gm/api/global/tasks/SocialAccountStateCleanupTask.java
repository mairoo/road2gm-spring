package kr.co.road2gm.api.global.tasks;

import kr.co.road2gm.api.domain.auth.repository.jpa.SocialAccountStateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class SocialAccountStateCleanupTask {
    private final SocialAccountStateRepository socialAccountStateRepository;

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    @Transactional
    public void cleanupExpiredStates() {
        log.info("Starting cleanup of expired auth states...");

        int deletedCount = socialAccountStateRepository.deleteAllCreatedBefore(LocalDateTime.now().minusMinutes(1));

        log.info("Cleanup completed. Deleted {} expired states", deletedCount);
    }
}
