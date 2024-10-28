package kr.co.road2gm.api.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class JwtTokenProvider {
    public static final String JWT_TYPE = "JWT";

    public static final String JWT_ALGORITHM = "HS512";

    // 알고리즘에 따라 키 길이 변경
    // HS256: openssl rand -hex 24
    // HS384: openssl rand -hex 32
    // HS512: openssl rand -hex 48
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expires-in}")
    private long accessTokenValidity;

    @Value("${jwt.refresh-token-expires-in}")
    private long refreshTokenValidity;

    private SecretKey key;

    private Map<String, Object> headers;

    @PostConstruct
    // 빈 컴포넌트 객체 생성자 후작업으로 멤버 변수 초기화
    public void init() {
        // SecretKey key = Jwts.SIG.HS512.key().build();
        // 위와 같이 키를 생성할 경우 서버 재부팅 시 액세스 토큰 검증 문제가 있을 수 있다.
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));

        headers = new HashMap<>();
        headers.put("typ", JWT_TYPE);
        headers.put("alg", JWT_ALGORITHM);
    }

    public String createAccessToken(String subject) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenValidity * 1000L);

        return Jwts.builder()
                .header()
                .add(headers)
                .and()
                .subject(subject) // 액세스 토큰에만 sub = username 존재
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    public String createRefreshToken() {
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenValidity * 1000L);

        return Jwts.builder()
                .header()
                .add(headers)
                .and()
                .issuedAt(now)
                .expiration(validity)
                .signWith(key)
                .compact();
    }

    public Optional<String> validateToken(String jws) {
        try {
            Jws<Claims> parsed = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(jws);

            return Optional.ofNullable(parsed.getPayload().getSubject());
        } catch (SignatureException | DecodingException ex) {
            // 잘못된 비밀키
            throw new RuntimeException("Invalid JWT signature");
        } catch (ExpiredJwtException ex) {
            // 만료된 토큰
            throw new RuntimeException("Expired JWT token");
        } catch (UnsupportedJwtException | MalformedJwtException | SecurityException | IllegalArgumentException ex) {
            // 토큰 형식 오류
            throw new RuntimeException("Invalid JWT token");
        }
    }

    public String getXAuthToken(HttpServletRequest request) {
        // Header format
        // Non-standard header
        // X-Auth-Token : JWTString=
        final String header = request.getHeader("X-AUTH-TOKEN");

        if (header != null && !header.isBlank()) {
            return header;
        }
        return null;
    }

    public String getBearerToken(HttpServletRequest request) {
        // Header format
        // RFC 7235 standard header
        // Authorization: Bearer JWTString=
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith("Bearer ")) {
            return header.split(" ")[1].trim();
        }

        return null;
    }
}
