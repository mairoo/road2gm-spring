package kr.co.road2gm.api.global.config;

import kr.co.road2gm.api.global.error.handlers.JwtAccessDeniedHandler;
import kr.co.road2gm.api.global.error.handlers.JwtAuthenticationEntryPoint;
import kr.co.road2gm.api.global.jwt.JwtAuthenticationFilter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
// URL 보안은 큰 범위의 정책에 사용
// - URL 기반의 보안 설정을 활성화
// - SecurityFilterChain을 구성하여 웹 보안을 설정
// - HTTP 요청에 대한 보안을 처리
// - Spring Security의 기본적인 웹 보안 기능을 활성화

@EnableMethodSecurity
// 메소드 보안은 세부적인 비즈니스 규칙에 사용
// - 메소드 레벨의 보안 설정을 활성화
// - @PreAuthorize, @PostAuthorize, @Secured, @RolesAllowed 등의 어노테이션 사용 가능
// - 기본값으로 prePostEnabled = false, securedEnabled = false, jsr250Enabled = false
// - Spring Security 5.6부터 @EnableGlobalMethodSecurity를 대체

@Getter
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    @Value("${security-config.content-security-policy}")
    private String contentSecurityPolicy;

    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    private final JwtAccessDeniedHandler accessDeniedHandler;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // WebSecurityConfigurerAdapter 상속 구현 방식은 더 이상 사용하지 않음
    // 현재는 @Bean 컴포넌트 설정 방식
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 로그인 인증 처리 관련 API

        // 폼 로그인 (사용 안 함)
        http.formLogin(AbstractHttpConfigurer::disable); // 스프링 시큐리티 6.1 이상 설정 방식
        // http.formLogin().disable(); // 스프링 시큐리티 6.0 설정 방식
        // http.logout()
        // http.rememberMe() // 전통적 세션 기반 인증에서 쿠키를 통한 세션 유지 (REST API 서버에서는 유효하지 않음)

        // HTTP Basic 인증 (사용 안 함)
        http.httpBasic(AbstractHttpConfigurer::disable);

        // CSRF (사용 안 함)
        // 액세스 토큰: 자바스크립트 비공개 객체에 보관
        // 리프레시 토큰: http only, same site strict 속성 쿠키 저장
        // http.csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"));
        http.csrf(AbstractHttpConfigurer::disable);

        // CORS
        // corsConfigurationSource
        http.cors(withDefaults());

        // Exception handling
        http.exceptionHandling(config -> {
            config.authenticationEntryPoint(authenticationEntryPoint) // 401 Unauthorized: 인증 실패
                    .accessDeniedHandler(accessDeniedHandler); // 403 Forbidden: 권한 없음
        });

        // HTTP 프로토콜 헤더
        http.headers(headers -> {
            headers.defaultsDisabled();

            // Strict-Transport-Security: max-age=31536000 ; includeSubDomains ; preload
            headers.httpStrictTransportSecurity(hstsConfig -> hstsConfig
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000)
                    .preload(true));

            //X-XSS-Protection: 1; mode=block
            headers.xssProtection(xssConfig -> xssConfig.headerValue(
                    XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK));

            // Cache-Control: no-cache, no-store, max-age=0, must-revalidate
            // Pragma: no-cache
            // Expires: 0
            headers.cacheControl(cacheControlConfig -> {
            });

            // Content-Security-Policy: default-src 'none'
            headers.contentSecurityPolicy(contentSecurityPolicyConfig ->
                                                  contentSecurityPolicyConfig.policyDirectives(contentSecurityPolicy));

            // X-Content-Type-Options: nosniff
            headers.contentTypeOptions(contentTypeOptionsConfig -> {
            });

            // X-Frame-Options: SAMEORIGIN | DENY
            headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin);
        });

        // Stateless Session
        http.sessionManagement(session -> {
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            // Login attempt throttling works in spite of stateless JWT authentication
            // maximumSessions(1): Prevents a user from logging in multiple times.
            // A second login will cause the first to be invalidated.
            // maxSessionsPreventsLogin(true): The second login will then be rejected
            // session.maximumSessions(1).maxSessionsPreventsLogin(true);
        });

        // Request resource permission mapping
        http.authorizeHttpRequests(auth -> auth
                                           // authorizing API examples
                                           // requestMatchers().hasRole().permitAll()
                                           // requestMatchers().denyAll()
                                           // .requestMatchers("/auth/**").permitAll()
                                           .requestMatchers("/**").permitAll()
                                           // anyRequest().authenticated() - rememberMe login enabled (form login)
                                           .anyRequest().fullyAuthenticated() //rememberMe disabled
                                  );

        // Add JWT token filter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder
    passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
