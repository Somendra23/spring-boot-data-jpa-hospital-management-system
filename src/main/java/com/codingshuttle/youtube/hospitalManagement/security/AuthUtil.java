package com.codingshuttle.youtube.hospitalManagement.security;

import com.codingshuttle.youtube.hospitalManagement.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class AuthUtil {

    @Value("${jwt.secret}")
    private String jwtSecretKey;

    public SecretKey getJwtSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(User user) {
        return Jwts.builder().subject(user.getUsername())
                .claim("userId", user.getId().toString())
                .issuedAt(new Date())
                //10 minutes() dealy 1000 * 60 * 10 , millisecond * 60 * 10
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                //refresh token
                .signWith(getJwtSecretKey()).compact();
    }

    public String getUserNameFromToken(String token) {
        Claims claims =  Jwts.parser()
                .verifyWith(getJwtSecretKey()).build()
                .parseSignedClaims(token)
                        .getPayload();
        return claims.getSubject();


    }
}
