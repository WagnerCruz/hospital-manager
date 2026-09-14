package com.raidstack.security;

import com.raidstack.services.exceptions.TokenExpiredException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

import static io.jsonwebtoken.security.Keys.secretKeyFor;

@Component
public class JwtTokenUtil {

    private final SecretKey jwtSecretKey = secretKeyFor(SignatureAlgorithm.HS256);

    private final int jwtExpirationInMs = 20000; // 1 hour

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new java.util.Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(jwtSecretKey)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtSecretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            throw new TokenExpiredException("JWT token has expired");
        } catch (io.jsonwebtoken.JwtException e) {
            return false;
        }
    }

}
