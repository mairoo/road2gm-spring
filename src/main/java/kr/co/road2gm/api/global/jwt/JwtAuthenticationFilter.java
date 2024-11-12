package kr.co.road2gm.api.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    private final UserDetailsService userDetailsService;

    public static final String BEARER_PREFIX = "Bearer ";

    public static final String X_AUTH_TOKEN = "X-Auth-Token";

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        // 매 요청마다 JWT 검증 오버헤드 발생 - 불필요한 토큰 파싱과 서명 검증
        // 스프링 시큐리티 설정에서 permitAll() 한 경로라고 해서 토큰 검증 필터가 실행이 안 되는 것이 아님
        //
        // 1. JwtAuthenticationFilter (토큰 검증)
        // 2. ... 다른 필터들 ...
        // 3. AuthorizationFilter (permitAll() 등의 권한 설정이 여기서 적용됨)

        List<RequestMatcher> permitAllMatchers = Arrays.asList(
                new AntPathRequestMatcher("/auth/**"),
                new AntPathRequestMatcher("/oauth2/**"),
                new AntPathRequestMatcher("/api/**"));

        return permitAllMatchers.stream()
                .anyMatch(matcher -> matcher.matches(request));
    }

    @Override
    protected void
    doFilterInternal(@NonNull HttpServletRequest request,
                     @NonNull HttpServletResponse response,
                     @NonNull FilterChain filterChain) throws ServletException, IOException {
        // 1. HTTP 프로토콜 헤더에서 토큰 추출
        Optional.ofNullable(getBearerToken(request))
                // 2. JWT 토큰 유효성 확인 및 username 추출
                .flatMap(jwtTokenProvider::validateToken)
                //
                .ifPresent(sub -> {
                    // 3. 데이터베이스에서 username 조회
                    UserDetails userDetails = userDetailsService.loadUserByUsername(sub);

                    // 4. Authenticate user in context
                    // AuthenticationManager or AuthenticationProvider implementation: isAuthenticated() = true

                    // 5. 인증 객체 생성
                    Authentication auth = new UsernamePasswordAuthenticationToken(userDetails,
                                                                                  null,
                                                                                  userDetails.getAuthorities());

                    // 6. WebAuthenticationDetails, WebAuthenticationDetailsSource 저장
                    // WebAuthenticationDetails 객체는 인증 요청과 관련된 웹 관련 정보
                    // - RemoteAddress (클라이언트 IP 주소)
                    // - SessionId (현재 세션 ID)
                    // setDetails()의 활용 용도
                    // - 특정 IP 주소나 지역에서의 접근 제한
                    // - 세션 기반의 추가적인 보안 검증
                    // - 사용자 행동 분석 및 로깅
                    // - 감사(audit) 기록 생성
                    // - 다중 요소 인증(MFA) 구현
                    // - IP 기반 접근 제한이나 차단
                    // authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 7. 현재 인증된 사용자 정보를 보안 컨텍스트에 저장 = 로그인 처리
                    SecurityContextHolder.getContext().setAuthentication(auth);

                    log.debug("logged in: {}", sub);
                });

        // 8. 이후 필터 실행
        filterChain.doFilter(request, response);
    }

    // 토큰 추출 우선순위 설정
    private String
    extractToken(HttpServletRequest request) {
        // Bearer 토큰과 쿠키의 우선순위:
        // - 어떤 것을 먼저 검사할지 명확히 정의
        // - 동시에 존재할 경우의 처리 방침 수립

        // 1. Bearer 토큰 먼저 확인
        String bearerToken = getBearerToken(request);
        if (bearerToken != null) {
            return bearerToken;
        }

        // 2. 쿠키 확인
        return getCookieToken(request);
    }

    private String
    getBearerToken(HttpServletRequest request) {
        // Header format
        // RFC 7235 standard header
        // Authorization: Bearer JWTString=
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length()).trim();
        }

        return null;
    }

    private String
    getCookieToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String
    getXAuthToken(HttpServletRequest request) {
        // Header format
        // Non-standard header
        // X-Auth-Token : JWTString=
        final String header = request.getHeader(X_AUTH_TOKEN);

        if (header != null && !header.isBlank()) {
            return header;
        }
        return null;
    }
}
