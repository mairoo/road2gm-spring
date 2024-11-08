package kr.co.road2gm.api.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;

@Configuration
@EnableScheduling
public class SchedulingConfig implements SchedulingConfigurer {
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());
    }

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

        scheduler.setPoolSize(2); // 동시에 실행될 수 있는 스케줄 작업 개수

        /* 스케줄러 추가 옵션
        scheduler.setThreadNamePrefix("scheduled-task-"); // 스레드 이름 접두사 설정
        scheduler.setAwaitTerminationSeconds(60); // 종료 대기 시간
        scheduler.setWaitForTasksToCompleteOnShutdown(true); // 종료 시 실행 중인 작업 완료 대기
        */

        return scheduler;
    }
}
