package i4U.mukPic.global.jwt.security;

import i4U.mukPic.global.auth.entity.Token;
import i4U.mukPic.global.jwt.service.TokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

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
        return generateToken(authentication.getName(), authentication.getAuthorities(), accessTokenExpirationTime);
    }

    public void generateRefreshToken(Authentication authentication, String accessToken) {
        String refreshToken = generateToken(authentication.getName(), authentication.getAuthorities(), refreshTokenExpirationTime);
        tokenService.saveOrUpdate(authentication.getName(), refreshToken, accessToken);
    }

    private String generateToken(String subject, Collection<? extends GrantedAuthority> authorities, long expirationTime) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        String roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

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
            return false;
        }

        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (JwtException e) {
            throw new RuntimeException("Invalid JWT Token");
        }
    }
}
