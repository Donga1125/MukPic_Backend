package i4U.mukPic.global.jwt.security;

import i4U.mukPic.global.auth.PrincipalDetails;
import i4U.mukPic.global.auth.entity.Token;
import i4U.mukPic.global.jwt.service.TokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret-key}")
    private String secretKeyString;

    @Value("${jwt.access.expiration-time}")
    private long accessTokenExpirationTime;

    @Value("${jwt.refresh.expiration-time}")
    private long refreshTokenExpirationTime;

    private SecretKey secretKey;
    private static final String KEY_ROLE = "role";
    private final TokenService tokenService;

    @PostConstruct
    private void setSecretKey() {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    public String generateAccessToken(Authentication authentication) {
        String subject;

        if (authentication.getPrincipal() instanceof PrincipalDetails principalDetails) {
            subject = principalDetails.user().getEmail(); // user 필드에서 이메일 가져오기
        } else {
            subject = authentication.getName(); // fallback
        }

        log.info("Generating Access Token for Subject: {}", subject);
        return generateToken(subject, authentication.getAuthorities(), accessTokenExpirationTime);
    }

    public void generateRefreshToken(Authentication authentication, String accessToken) {
        // 로그 추가: Authentication의 Name이 이메일인지 확인
        log.info("Generating Refresh Token for: {}", authentication.getName());
        String refreshToken = generateToken(authentication.getName(), authentication.getAuthorities(), refreshTokenExpirationTime);
        tokenService.saveOrUpdate(authentication.getName(), refreshToken, accessToken);
    }

    private String generateToken(String subject, Collection<? extends GrantedAuthority> authorities, long expirationTime) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        String roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // 로그 추가: Token 생성 시 Subject와 Role 정보 확인
        log.info("Generating Token - Subject: {}, Roles: {}", subject, roles);

        return Jwts.builder()
                .setSubject(subject)
                .claim(KEY_ROLE, roles)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        List<SimpleGrantedAuthority> authorities = getAuthorities(claims);

        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    private List<SimpleGrantedAuthority> getAuthorities(Claims claims) {
        return Arrays.stream(claims.get(KEY_ROLE).toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    public String reissueAccessToken(String accessToken) {
        if (StringUtils.hasText(accessToken)) {
            Token token = tokenService.findByAccessTokenOrThrow(accessToken);
            String refreshToken = token.getRefreshToken();

            if (validateToken(refreshToken)) {
                String newAccessToken = generateAccessToken(getAuthentication(refreshToken));
                tokenService.updateToken(newAccessToken, token);
                return newAccessToken;
            }
        }
        return null;
    }

    public boolean validateToken(String token) {
        if (!StringUtils.hasText(token)) {
            log.warn("Token is empty or null.");
            return false;
        }

        try {
            Claims claims = parseClaims(token);
            log.info("Valid token. Claims: {}", claims); // 추가 로그
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Token is expired: {}", e.getMessage());
        } catch (JwtException e) {
            log.warn("Invalid token: {}", e.getMessage());
        }
        return false;
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.error("JWT Token is expired: {}", e.getMessage());
            throw e;
        } catch (UnsupportedJwtException e) {
            log.error("JWT Token is unsupported: {}", e.getMessage());
            throw e;
        } catch (MalformedJwtException e) {
            log.error("JWT Token is malformed: {}", e.getMessage());
            throw e;
        } catch (io.jsonwebtoken.security.SecurityException e) { // 대체 예외 클래스 사용
            log.error("JWT Token signature is invalid: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty or invalid: {}", e.getMessage());
            throw e;
        }
    }


    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("Authorization"))
                .filter(authHeader -> authHeader.startsWith("Bearer "))
                .map(authHeader -> authHeader.substring(7));
    }

    public Optional<String> extractSubject(String accessToken) {
        try {
            Claims claims = parseClaims(accessToken);

            // 로그 추가: Subject(이메일)가 제대로 저장되었는지 확인
            log.info("Extracted Email (Subject) from Token: {}", claims.getSubject());

            return Optional.ofNullable(claims.getSubject()); // Subject에 이메일이 저장되어 있는지 확인
        } catch (JwtException e) {
            log.error("Error extracting email from token: {}", e.getMessage());
            return Optional.empty();
        }
    }

}
