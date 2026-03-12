package com.codingshuttle.youtube.hospitalManagement.security;

import com.codingshuttle.youtube.hospitalManagement.entity.ProviderType;
import com.codingshuttle.youtube.hospitalManagement.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
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

    public ProviderType getProviderTypeFromRegistrationId(String registrationId){
        return switch (registrationId.toLowerCase()){
            case "google" -> ProviderType.GOOGLE;
            case "facebook" -> ProviderType.FACEBOOK;
            case "github" -> ProviderType.GITHUB;
            default ->   throw new IllegalArgumentException("Invalid provider type");
        };
    }

    public String determineProviderIdFromOAuth2User(OAuth2User oAuth2User, String registrationId){
        String providerId = switch (registrationId.toLowerCase()){
            case "google" -> oAuth2User.getAttribute("sub");
            case "facebook" -> oAuth2User.getAttribute("id");
            case "github" -> oAuth2User.getAttribute("id");
            default ->   throw new IllegalArgumentException("Invalid provider type");
        };
        if (providerId ==null || providerId.isEmpty()){
            log.error("Provider id cannot be null or empty for registration id: {}", registrationId);
            throw new IllegalArgumentException("Provider id cannot be null or empty");
        }
        return providerId;
    }

    public String determineUsernameFromOAuth2User(OAuth2User oAuth2User, String registrationId, String providerId){
        String email = oAuth2User.getAttribute("email");
        if (email!=null && !email.isBlank()){
            return email;
        }
        return switch (registrationId.toLowerCase()){
            case "google" -> oAuth2User.getAttribute("sub");
            case "facebook" -> oAuth2User.getAttribute("login");
            case "github" -> oAuth2User.getAttribute("login");
            default ->   throw new IllegalArgumentException("Invalid provider type");
        };
    }
}
