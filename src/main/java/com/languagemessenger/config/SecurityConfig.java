package com.languagemessenger.config;

import jakarta.servlet.http.Cookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.languagemessenger.security.JwtAuthenticationFilter;

/**
 * Конфигурационный класс для настройки безопасности приложения Language Messenger.
 * Осуществляет настройку Spring Security, включая управление аутентификацией,
 * шифрование паролей, фильтры JWT и политику сессий.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Создаёт и возвращает объект шифратора паролей, использующий алгоритм BCrypt.
     * Этот бин используется для безопасного хранения и проверки паролей пользователей.
     *
     * @return экземпляр {@link PasswordEncoder} с реализацией BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Настраивает и возвращает менеджер аутентификации для обработки процесса входа в систему.
     * Использует конфигурацию аутентификации из Spring Security.
     *
     * @param authenticationConfiguration конфигурация аутентификации, предоставляемая Spring
     * @return экземпляр {@link AuthenticationManager} для управления аутентификацией
     * @throws Exception если возникает ошибка при получении менеджера аутентификации
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Настраивает цепочку фильтров безопасности для приложения.
     * Отключает CSRF, устанавливает безсессионную политику (STATLESS),
     * определяет доступ к эндпоинтам и добавляет фильтр JWT перед стандартным фильтром аутентификации.
     * В случае ошибки аутентификации очищает JWT cookie и перенаправляет на страницу логина.
     *
     * @param http объект конфигурации HTTP-запросов Spring Security
     * @param jwtAuthenticationFilter кастомный фильтр для проверки JWT-токенов
     * @return сконфигурированная цепочка фильтров безопасности {@link SecurityFilterChain}
     * @throws Exception если возникает ошибка при настройке безопасности
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/web/register", "/web/register/**", "/web/login", "/api/cities", "web/home/api/**", "/api/**", "/resources/**", "/error").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            // Очищаем cookie JWT
                            Cookie jwtCookie = new Cookie("JWT", null);
                            jwtCookie.setHttpOnly(true);
                            jwtCookie.setPath("/");
                            jwtCookie.setMaxAge(0);
                            response.addCookie(jwtCookie);
                            response.sendRedirect("/web/login");
                        })
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}