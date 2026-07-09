package com.goMaddy.multithreaded_http_fileserver.security;

import com.goMaddy.multithreaded_http_fileserver.entity.User;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {
    private static final String SECRET = "THIS_IS_A_DEMO_SECRET_KEY_FOR_JWT_CHANGE_IN_PRODUCTION_123456789";
    private static final long EXPIRATION = 1000 * 60 * 60; // 1 hour
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(
                SECRET.getBytes()
        );
    }
    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSigningKey())
                .compact();
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    public <T> T extractClaim(
            String token,
            Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    public boolean isTokenExpired(String token) {
        return extractExpiration(token)
                .before(new Date());
    }
    public boolean isTokenValid(String token, User user) {
        String tokenEmail = extractEmail(token);
        return tokenEmail.equals(user.getEmail())
                && !isTokenExpired(token);
    }
}
