# Language Exchange Messenger

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15+-blue.svg)](https://www.postgresql.org/)
[![WebSocket](https://img.shields.io/badge/WebSocket-STOMP-yellow.svg)](https://spring.io/guides/gs/messaging-stomp-websocket/)
[![Security](https://img.shields.io/badge/Security-JWT-orange.svg)](https://jwt.io/)

**Language Exchange Messenger** — это полнофункциональное веб-приложение для языкового обмена, построенное на основе Spring Boot. Оно позволяет пользователям находить партнеров для практики иностранных языков, общаться в реальном времени в защищенных чатах и использовать встроенный переводчик.

## ✨ Ключевые особенности

*   **🔐 Безопасная аутентификация и авторизация:** Регистрация и вход с использованием JWT (JSON Web Token), хранящегося в HTTP-only cookie. Пароли надежно хешируются с помощью BCrypt.
*   **🗂️ Профиль пользователя:**
    *   Указание родного города и страны.
    *   Настройка языков для изучения (с указанием уровня от A1 до C2).
    *   Настройка языков для преподавания (с указанием уровня C1, C2 или Native).
*   **👥 Умный поиск партнеров:** Алгоритм поиска находит пользователей, которые преподают язык, который вы изучаете, и при этом сами изучают язык, который вы преподаете (взаимный интерес).
*   **💬 Веб-сокет чаты в реальном времени:** Мгновенный обмен сообщениями с использованием STOMP поверх WebSocket. История сообщений загружается при входе в чат.
*   **🛡️ Сквозное шифрование (End-to-End Encryption):** Сообщения шифруются на стороне клиента с помощью гибридной схемы (RSA + AES). В базе данных хранятся только зашифрованные тексты и ключи.
*   **🌍 Интеграция с переводчиком:** Встроенная функция перевода сообщений через внешнее API ([Lingva](https://github.com/thedaviddelta/lingva-translate)) для преодоления языковых барьеров. Пользователь может выбрать предпочтительный язык перевода.
*   **📊 Уровни владения языком (CEFR):** Поддержка стандартной системы уровней от A1 (Beginner) до C2 (Proficiency/Native).

## 🛠️ Технологический стек

*   **Бэкенд:** Spring Boot 3, Spring Security, Spring Data JPA, Spring WebSocket (STOMP)
*   **База данных:** PostgreSQL
*   **Фронтенд:** Thymeleaf (для рендеринга шаблонов), JavaScript, SockJS, Stomp.js
*   **Аутентификация:** JWT (JSON Web Token)
*   **Шифрование:** RSA (для обмена ключами), AES (для шифрования сообщений)
*   **Перевод:** Внешнее API Lingva Translate
*   **Сборка:** Maven

## 📋 Предварительные требования

Перед запуском приложения убедитесь, что на вашем компьютере установлено:
*   **JDK 17** или выше
*   **Maven 3.6+**
*   **PostgreSQL 15+**
*   **Git**

## 🚀 Установка и запуск

1.  **Клонируйте репозиторий:**
    ```bash
    git clone <your-repository-url>
    cd language-exchange-messenger
    ```

2.  **Настройте базу данных PostgreSQL:**
    *   Создайте новую базу данных с именем `messengerDB`.
    *   Выполните SQL-скрипт из файла `schema.sql` (приведен в начале вашего описания), чтобы создать все необходимые таблицы, ограничения и связи.

3.  **Настройте файл `application.properties`:**
    Обновите файл `src/main/resources/application.properties`, указав актуальные учетные данные для вашей базы данных:
    ```properties
    spring.datasource.url=jdbc:postgresql://localhost:5432/messengerDB
    spring.datasource.username=your_postgres_username
    spring.datasource.password=your_postgres_password
    ```

4.  **Соберите и запустите приложение:**
    ```bash
    mvn clean spring-boot:run
    ```

5.  **Откройте приложение в браузере:**
    Перейдите по адресу [http://localhost:8080](http://localhost:8080)

## 📁 Структура проекта



<img width="1171" height="342" alt="image" src="https://github.com/user-attachments/assets/e17db8d3-f7d5-4a82-bd6d-e702ba0e6ee1" />
