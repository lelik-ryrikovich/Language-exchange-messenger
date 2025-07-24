package com.languagemessenger.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {
    private final Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Генерация ключа
    private final long JWT_EXPIRATION = 1000 * 60 * 60 * 10; // 10 часов

    /**
     * Генерирует JWT-токен для указанного пользователя.
     *
     * @param username имя пользователя для включения в токен
     * @return сгенерированный JWT-токен
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    /**
     * Создаёт JWT-токен с указанными утверждениями и субъектом.
     *
     * @param claims дополнительные утверждения для токена
     * @param subject субъект токена (обычно имя пользователя)
     * @return сгенерированный JWT-токен
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Проверяет валидность JWT-токена для указанного пользователя.
     *
     * @param token JWT-токен для проверки
     * @param username имя пользователя для сопоставления
     * @return true, если токен валиден и соответствует пользователю, иначе false
     */
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    /**
     * Извлекает имя пользователя из JWT-токена.
     *
     * @param token JWT-токен для анализа
     * @return имя пользователя, указанное в токене
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Извлекает дату истечения срока действия из JWT-токена.
     *
     * @param token JWT-токен для анализа
     * @return дата истечения срока действия токена
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Извлекает конкретное утверждение из JWT-токена с использованием заданной функции.
     *
     * @param token JWT-токен для анализа
     * @param claimsResolver функция для извлечения нужного утверждения
     * @param <T> тип возвращаемого значения
     * @return значение утверждения
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Извлекает все утверждения из JWT-токена.
     *
     * @param token JWT-токен для анализа
     * @return объект Claims, содержащий все утверждения токена
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Проверяет, истёк ли срок действия JWT-токена.
     *
     * @param token JWT-токен для проверки
     * @return true, если токен истёк, иначе false
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}