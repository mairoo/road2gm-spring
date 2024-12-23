package kr.co.road2gm.api.global.config;

import kr.co.road2gm.api.global.security.handlers.JwtAccessDeniedHandler;
import kr.co.road2gm.api.global.security.handlers.JwtAuthenticationEntryPoint;
import kr.co.road2gm.api.global.security.jwt.JwtAuthenticationFilter;
import kr.co.road2gm.api.global.security.oauth2.service.CustomOAuth2UserService;
import kr.co.road2gm.api.global.security.oauth2.handlers.OAuth2SuccessHandler;
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

    private final CustomOAuth2UserService customOAuth2UserService;

    private final OAuth2SuccessHandler oAuth2SuccessHandler;

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

        // JWT 기반 REST API 그리고 Remember Me
        // STATELESS 세션 상태를 유지하지 않는 것이 원칙
        // 리프레시 토큰이 이미 Remember Me와 유사한 역할 수행
        // 세션을 사용하지 않으므로 스프링 시큐리티 Remember Me 설정은 효과 없음
        //
        // JWT 기반 REST API: 로그인 시 Remember Me 유무에 따라 리프레시 토큰의 유효기간을 다르게 설정하는 기법 사용

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
            // 스프링 시큐리티가  무조건 세션을 생성하지 않는다는 의미가 아님
            // 인증 처리 관점에서 세션을 생성하지 않음과 동시에 세션을 이용한 방식으로 인증을 처리하지 않는다는 뜻
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            // Login attempt throttling works in spite of stateless JWT authentication
            // maximumSessions(1): Prevents a user from logging in multiple times.
            // A second login will cause the first to be invalidated.
            // maxSessionsPreventsLogin(true): The second login will then be rejected
            // session.maximumSessions(1).maxSessionsPreventsLogin(true);
        });

        // Request resource permission mapping
        http.authorizeHttpRequests(auth -> auth
                                           // 1. 공개 리소스
                                           .requestMatchers("/auth/**",
                                                            "/oauth2/**",
                                                            "/api/**").permitAll()
                                           // 2. 상기 경로 이외 모두 비공개 리소스
                                           .anyRequest().authenticated()
                                  );

        // Add JWT token filter
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.oauth2Login(oauth2 -> {
            // oauth2Login()만 설정해도 기본 인증 엔드포인트 [http://localhost:8080/oauth2/authorization/google] 활성화
            oauth2.userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
                    .successHandler(oAuth2SuccessHandler);
        });

        return http.build();
    }

    @Bean
    public PasswordEncoder
    passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
