package com.languagemessenger.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import jakarta.servlet.http.Cookie;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    /**
     * Выполняет фильтрацию каждого запроса для проверки JWT-токена из cookie.
     * Пропускает запросы к /web/login и /web/register, для остальных извлекает токен,
     * валидирует его и устанавливает аутентификацию в SecurityContext, если токен валиден.
     *
     * @param request HTTP-запрос
     * @param response HTTP-ответ
     * @param filterChain цепочка фильтров для продолжения обработки запроса
     * @throws ServletException если возникает ошибка сервлета
     * @throws IOException если возникает ошибка ввода-вывода
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Пропускаем фильтр для /web/login и /web/register
        String path = request.getRequestURI();
        logger.debug("Processing request for path: " + path);
        if ("/web/login".equals(path) || "/web/register".equals(path)) {
            logger.debug("Skipping JWT filter for path: " + path);
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("JWT".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                    logger.debug("Found JWT cookie: " + jwt);
                    break;
                }
            }
        }

        if (jwt == null || jwt.isBlank()) {
            logger.debug("No JWT token found or token is blank");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String username = jwtUtil.extractUsername(jwt);
            logger.debug("Extracted username from JWT: " + username);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.debug("Authenticated user: " + username);
                } else {
                    logger.warn("JWT token validation failed for username: " + username);
                }
            }
        } catch (Exception e) {
            logger.warn("Invalid JWT token: " + e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }
}