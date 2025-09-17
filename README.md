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
    *   Выполните SQL-скрипты `messenger_db.sql`, `insert_queries.sql` из папки `Database`, чтобы создать и инициализировать все необходимые таблицы, ограничения и связи.

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
    Перейдите по адресу [http://localhost:8080/web/login](http://localhost:8080/web/login)

## 📁 Структура проекта
src/
├── main/
│ ├── java/com/languagemessenger/
│ │ ├── config/ # Конфигурационные классы (Security, WebSocket)
│ │ ├── controller/ # Контроллеры (Web, REST API, WebSocket)
│ │ ├── model/ # Сущности JPA (AppUser, Chat, Message, etc.)
│ │ ├── repository/ # Интерфейсы Spring Data JPA репозиториев
│ │ ├── service/ # Бизнес-логика
│ │ ├── security/ # JWT утилиты и фильтры
│ │ └── dto/ # Data Transfer Objects (MessageDto)
│ ├── resources/
│ │ ├── static/ # библиотеки JS для криптографии
│ │ ├── templates/ # Thymeleaf HTML шаблоны
│ │ └── application.properties

## 🔐 Безопасность и шифрование

Приложение использует многоуровневый подход к безопасности:
1.  **Spring Security & JWT:** Контроль доступа к эндпоинтам и аутентификация.
2.  **Шифрование паролей:** Пароли пользователей хешируются с помощью BCrypt.
3.  **Сквозное шифрование (E2EE):**
    *   Каждый пользователь при регистрации получает пару RSA-ключей (публичный и приватный).
    *   Для каждого сообщения генерируется случайный AES-ключ.
    *   AES-ключ шифруется публичным ключом получателя и отправителя (для истории чата).
    *   Текст сообщения шифруется с помощью AES.
    *   В базе данных хранятся только зашифрованный текст (`encrypted_text`) и зашифрованные ключи (`encrypted_aes_key_recipient`, `encrypted_aes_key_sender`).

## 🌐 API перевода

Для перевода текста используется открытое API Lingva Translate (`https://translate.plausibility.cloud`). Пользователь может выбрать язык, на который он хочет переводить входящие сообщения (например, русский, английский, испанский и т.д.).

## 🎯 Как это работает

1.  **Регистрация:** Пользователь создает аккаунт, указывает свои данные, город и генерирует пару RSA-ключей.
<img width="1126" height="612" alt="image" src="https://github.com/user-attachments/assets/f9efa5e1-64d6-4a6a-aa11-958cabfe76b2" />
2. **Авторизация** Пользователь вводит данные своего аккаунта и нажимает кнопку войти.
<img width="1171" height="342" alt="image" src="https://github.com/user-attachments/assets/e17db8d3-f7d5-4a82-bd6d-e702ba0e6ee1" />
2.  **Настройка языков:** В своем профиле пользователь добавляет языки, которые он хочет изучать, и языки, которые может преподавать, с указанием уровня владения.
<img width="1111" height="958" alt="image" src="https://github.com/user-attachments/assets/c1efbf23-776f-4b24-b584-4a4569a0a512" />
3.  **Поиск партнеров:** Пользователь выбирает язык и уровень, который хочет практиковать. Система ищет пользователей, которые преподают этот язык на выбранном уровне и при этом изучают язык, которым владеет текущий пользователь.
<img width="1074" height="512" alt="image" src="https://github.com/user-attachments/assets/6978f53b-83bf-4f29-861a-ac1e709f10ba" />
4.  **Чат:** Пользователь может начать чат с найденным партнером. Сообщения шифруются и расшифровываются на лету в браузере.
<img width="1074" height="312" alt="image" src="https://github.com/user-attachments/assets/9c37a140-f454-4d98-9b8a-a5c9eb8e7238" />
5.  **Перевод:** Полученные сообщения можно перевести на выбранный язык прямо в интерфейсе чата.
<img width="1120" height="692" alt="image" src="https://github.com/user-attachments/assets/8750c2f7-9b5d-4e3c-83b2-281fef23d4bb" />







