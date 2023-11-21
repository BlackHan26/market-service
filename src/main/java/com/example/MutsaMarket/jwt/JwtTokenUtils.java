package com.example.MutsaMarket.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenUtils {
    private final Key signingKey;
    private final JwtParser jwtParser;
    public JwtTokenUtils(
            @Value("${jwt.secret}")
            String jwtSecret
    ) {
        this.signingKey
                = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        this.jwtParser = Jwts
                .parserBuilder()
                .setSigningKey(this.signingKey)
                .build();
    }
    public boolean validate(String token) {
        try {
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (Exception e){
            log.warn("invalid jwt");
        }
        return false;
    }
    public String generateToken(UserDetails userDetails) {
        // JWT에 담고싶은 추가 정보를 여기에서 추가한다.
        Claims jwtClaims = Jwts.claims()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(Date.from(Instant.now().plusSeconds(10)));

        return Jwts.builder()
                .setClaims(jwtClaims)
                .signWith(signingKey)
                .compact();
    }
    // JWT를 인자로 받고, 그 JWT를 해석해서
// 사용자 정보를 회수하는 메소드
    public Claims parseClaims(String token) {
        return jwtParser
                .parseClaimsJws(token)
                .getBody();
    }
}
