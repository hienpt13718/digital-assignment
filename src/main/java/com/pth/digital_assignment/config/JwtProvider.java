package com.pth.digital_assignment.config;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.pth.digital_assignment.config.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Setter
@Component
@RequiredArgsConstructor
public class JwtProvider {
    private final JsonMapper jsonMapper;
    private final JwtProperties jwtProperties;

    public String generateToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        SecretKey key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());

        Date expirationDate = jwtProperties.getExpirationFromNow();

        return Jwts.builder()
                .subject(userPrincipal.getUsername())
                .claim("identifier", userPrincipal.getUsername())
                .claim("authorities", userPrincipal.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .issuedAt(new Date())
                .expiration(expirationDate)
                .signWith(key, Jwts.SIG.HS256).compact();
    }

    public UUID getUserIdFromJWT(String token) {
        Claims claims = getClaims(token);
        return UUID.fromString(claims.getSubject());
    }

    public Claims getClaims(String token) {
        JwtParser jwtParser = getJwtParser(jwtProperties.getSecret());
        Jws<Claims> jwsClaim = jwtParser.parseSignedClaims(token);

        return jwsClaim.getPayload();
    }

    private JwtParser getJwtParser(String jwtSecret) {
        SecretKey secretBytes = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.parser().verifyWith(secretBytes).build();
    }

    public boolean validate(String token) {
        try {
            JwtParser jwtParser = getJwtParser(jwtProperties.getSecret());
            jwtParser.parseSignedClaims(token);

            return true;
        } catch (SecurityException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}
