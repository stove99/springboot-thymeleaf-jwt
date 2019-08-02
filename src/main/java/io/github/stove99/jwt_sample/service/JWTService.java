package io.github.stove99.jwt_sample.service;

import java.security.Key;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JWTService {
    // application.properties 에 secret 설정, 대충 원하는 문자열 10~20글자정도?
    @Value("${site.jwt.secret}")
    private String secret;

    /**
     * body 가 들어간 토큰 생성
     * 
     * @param body
     * @param expired 토근 만료 시간
     * @return
     */
    public String token(Map<String, Object> body, Optional<LocalDateTime> expired) {
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secret);

        Key key = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS512.getJcaName());

        JwtBuilder builder = Jwts.builder().setClaims(body)
                .setExpiration(Timestamp.valueOf(LocalDateTime.now().plusDays(1)))
                .signWith(SignatureAlgorithm.HS512, key);

        // 만료시간을 설정할 경우 expir 설정
        expired.ifPresent(exp -> {
            builder.setExpiration(Timestamp.valueOf(exp));
        });

        return builder.compact();
    }

    /**
     * 기본 만료시간 : 하루 30분 : LocalDateTime.now().plusMinutes(30) 1시간 :
     * LocalDateTime.now().plusHours(1)
     * 
     * @param body
     * @return
     */
    public String token(Map<String, Object> body) {
        return token(body, Optional.of(LocalDateTime.now().plusDays(1)));
    }

    /**
     * 토큰 검증후 저장된 값 복원
     */
    public Map<String, Object> verify(String token) {
        Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(secret)).parseClaimsJws(token)
                .getBody();

        return new HashMap<>(claims);
    }
}