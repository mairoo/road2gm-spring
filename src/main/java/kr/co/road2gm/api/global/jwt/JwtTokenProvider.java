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
    private long accessTokenValidityInMilliseconds;

    @Value("${jwt.refresh-token-expires-in}")
    private long refreshTokenValidityInMilliseconds;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String createAccessToken(String username) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", JWT_TYPE);
        headers.put("alg", JWT_ALGORITHM);

        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);

        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .header()
                .add(headers)
                .and()
                .claims(claims)
                .issuedAt(now)
                .expiration(validity)
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshToken() {
        Map<String, Object> headers = new HashMap<>();
        headers.put("typ", JWT_TYPE);
        headers.put("alg", JWT_ALGORITHM);

        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenValidityInMilliseconds);

        return Jwts.builder()
                .header()
                .add(headers)
                .and()
                .issuedAt(now)
                .expiration(validity)
                .signWith(secretKey)
                .compact();
    }

    public Optional<String> getUsername(String token) {
        try {
            Jws<Claims> jws = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);

            return Optional.ofNullable(jws.getPayload().getSubject());
        } catch (SignatureException | DecodingException | ExpiredJwtException | MalformedJwtException |
                 UnsupportedJwtException | SecurityException | IllegalArgumentException e) {
            throw new RuntimeException();
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
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
