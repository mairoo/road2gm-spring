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
                    // authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 7. 보안 컨텍스트에 사용자 저장 = 로그인 처리
                    SecurityContextHolder.getContext().setAuthentication(auth);
                });

        // 8. 이후 필터 실행
        filterChain.doFilter(request, response);
    }
}
