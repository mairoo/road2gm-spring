package kr.co.road2gm.api.global.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    private final UserDetailsService userDetailsService;

    @Override
    protected void
    doFilterInternal(@NonNull HttpServletRequest request,
                     @NonNull HttpServletResponse response,
                     @NonNull FilterChain filterChain) throws ServletException, IOException {
        // 1. HTTP 프로토콜 헤더에서 토큰 추출
        Optional.ofNullable(jwtTokenProvider.getBearerToken(request))
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
                });

        // 8. 이후 필터 실행
        filterChain.doFilter(request, response);
    }
}
