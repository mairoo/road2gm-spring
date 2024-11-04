package kr.co.road2gm.api.global.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class QueryDslConfig {
    @PersistenceContext
    private final EntityManager em;

    // DataSource bean injection by Spring boot 'Auto configuration' with `application.yaml`

    // JdbcTemplate bean injection by `spring-boot-starter-jdbc` (DataSource injected)
    // Instances of the JdbcTemplate class are thread-safe, once configured.
    // This is important because it means that you can configure a single instance of a JdbcTemplate
    // and then safely inject this shared reference into multiple DAOs (or repositories).
    // The JdbcTemplate is stateful, in that it maintains a reference to a DataSource,
    // but this state is not "conversational state (stateless session)".

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        // QueryDSL configuration
        // Concurrency issue
        // EntityManager DI when creating JPAQueryFactory instance
        // EntityManager instance separately / concurrently works in a transaction unit
        return new JPAQueryFactory(em);
    }

}
