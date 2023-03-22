package com.innowise.task03.service;

import com.innowise.task03.entity.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.time.Instant;
import java.util.Date;

public class JwtService {

    private final String SECRET = "Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E=";
    private static volatile JwtService instance;

    public static JwtService getInstance() {
        JwtService localInstance = instance;
        if (localInstance == null) {
            synchronized (JwtService.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new JwtService();
                }
            }
        }
        return localInstance;
    }

    public String buildJwtForUser(User user){
        return Jwts.builder()
                .setIssuer("Zidane")
                .claim("user", user.getLogin())
                .claim("role", user.getRole())
                .setIssuedAt(Date.from(Instant.ofEpochSecond(1466796822L)))
                .setExpiration(Date.from(Instant.ofEpochSecond(4622470422L)))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET))
                )
                .compact();
    }

    public Jws<Claims> verifyJwtForUser(String jwsString) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET)))
                .build()
                .parseClaimsJws(jwsString);



    }


}
