package kr.co.road2gm.api.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing // created, modified 필드 = Django 호환
public class JpaConfig {
}
