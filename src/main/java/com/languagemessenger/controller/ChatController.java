package com.languagemessenger.controller;

import com.languagemessenger.dto.MessageDto;
import com.languagemessenger.model.*;
import com.languagemessenger.repository.*;
import com.languagemessenger.service.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Контроллер для управления чатом и переводами в приложении Language Messenger.
 * Обрабатывает WebSocket-сообщения, историю чата, перевод текста через внешнее API
 * и предоставляет доступ к доступным языкам перевода.
 */
@Controller
@RequestMapping("/web/home")
@RequiredArgsConstructor
public class ChatController {
    private final AppUserService appUserService;
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private static final Logger LOGGER = Logger.getLogger(ChatController.class.getName());
    private final RestTemplate restTemplate = new RestTemplate(); // Для HTTP-запросов к Lingva

    /**
     * Обрабатывает отправку сообщения через WebSocket и рассылает его всем подписанным клиентам.
     * Проверяет существование сообщения и пользователя, сохраняет его в базе данных и
     * возвращает обновлённый DTO с информацией об отправителе и временной меткой.
     *
     * @param chatId идентификатор чата, для которого отправляется сообщение
     * @param messageDto объект DTO с данными сообщения (текст, ключи шифрования и т.д.)
     * @return обновлённый {@link MessageDto} с ID, именем отправителя и временной меткой
     */
    @MessageMapping("/chat/{chatId}")
    @SendTo("/topic/messages/{chatId}")
    public MessageDto sendMessage(@DestinationVariable Integer chatId, MessageDto messageDto) {
        if (messageDto.getId() != null && messageRepository.existsById(messageDto.getId())) {
            return messageDto;
        }
        AppUser sender = appUserService.getUserById(messageDto.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Message message = new Message();
        message.setEncryptedText(messageDto.getEncryptedText());
        message.setEncryptedAesKeyRecipient(messageDto.getEncryptedAesKeyRecipient());
        message.setEncryptedAesKeySender(messageDto.getEncryptedAesKeySender());
        message.setIv(messageDto.getIv());

        message = appUserService.sendMessage(sender, chatId, message);

        messageDto.setId(message.getId());
        messageDto.setSenderName(sender.getNickname());
        messageDto.setTimestamp(message.getDateOfSending().toString());
        messageDto.setEncryptedText(message.getEncryptedText());
        messageDto.setEncryptedAesKeyRecipient(message.getEncryptedAesKeyRecipient());
        messageDto.setEncryptedAesKeySender(message.getEncryptedAesKeySender());
        messageDto.setIv(message.getIv());

        return messageDto;
    }

    /**
     * Возвращает список сообщений для указанного чата в порядке возрастания времени отправки.
     * Преобразует сущности сообщений в DTO для передачи клиенту.
     *
     * @param chatId идентификатор чата, сообщения которого нужно получить
     * @return список {@link MessageDto} с данными сообщений
     * @throws IllegalArgumentException если чат не найден
     */
    @GetMapping("/api/chats/{chatId}/messages")
    @ResponseBody
    public List<MessageDto> getChatMessages(@PathVariable Integer chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found"));

        return messageRepository.findByChatOrderByDateOfSendingAsc(chat).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Обрабатывает запрос на перевод текста с использованием внешнего API (Lingva).
     * Использует Map для гибкой обработки JSON-запроса, где ключ "text" содержит текст для перевода.
     * Определяет целевой язык на основе профиля текущего пользователя и возвращает
     * переведённый текст вместе с обнаруженным языком источника.
     *
     * @param request map с полем "text" для перевода, поддерживает дополнительные параметры в будущем
     * @return {@link ResponseEntity} с переведённым текстом и обнаруженным языком
     *         или ошибкой, если перевод не удался
     */
    @PostMapping("/api/translate")
    @ResponseBody
    public ResponseEntity<Map<String, String>> translateText(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        if (text == null || text.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Text is required"));
        }

        try {
            // Получаем текущего пользователя и его целевой язык перевода
            AppUser user = appUserService.getCurrentUser();
            String targetLanguage = appUserService.getTranslationLanguage(user);
            LOGGER.info("Translating to language: " + targetLanguage);
            text = text.replace("?", "%3F");

            // Используем исходный текст без предварительного кодирования
            String url = "https://translate.plausibility.cloud/api/v1/auto/" + targetLanguage + "/" + text;

            LOGGER.info("Request URL: " + url);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Accept-Language", "en-US,en;q=0.9");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            Map<String, Object> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, entity, Map.class).getBody();
            LOGGER.info("Raw response: " + response);

            String translatedText = response != null && response.containsKey("translation")
                    ? String.valueOf(response.get("translation"))
                    : null;
            if (translatedText == null || translatedText.isEmpty()) {
                return ResponseEntity.status(500).body(Map.of("error", "No translation received from Lingva"));
            }

            // Проверяем на закодированные символы и декодируем
            if (translatedText.matches(".*%[0-9A-Fa-f]{2}.*")) {
                LOGGER.info("Detected encoded translation, retrying with decoded text");
                try {
                    translatedText = URLDecoder.decode(translatedText, "UTF-8");
                } catch (Exception e) {
                    LOGGER.warning("Failed to decode translation: " + e.getMessage());
                }
            }

            // Очистка текста от нежелательных символов
            try {
                translatedText = URLDecoder.decode(translatedText, "UTF-8");
                translatedText = translatedText.replace(" ~", "?");
                translatedText = translatedText.replace("~", "?");
                //translatedText = translatedText.replaceAll("[^\\p{L}\\p{N}\\p{P}\\p{Z}]", "");
            } catch (Exception e) {
                LOGGER.warning("Failed to clean translation: " + e.getMessage());
            }
            LOGGER.info("Cleaned translation: " + translatedText);

            String detectedLanguage = response != null && response.containsKey("info") && ((Map) response.get("info")).containsKey("detectedSource")
                    ? String.valueOf(((Map) response.get("info")).get("detectedSource"))
                    : "unknown";

            Map<String, String> result = new HashMap<>();
            result.put("translatedText", translatedText);
            result.put("detectedLanguage", detectedLanguage);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            LOGGER.severe("Translation failed: " + e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Translation failed: " + e.getMessage()));
        }
    }

    /**
     * Преобразует сущность сообщения в DTO для передачи клиенту.
     * Копирует основные поля, включая ID, имя отправителя и данные шифрования.
     *
     * @param message сущность сообщения для преобразования
     * @return объект {@link MessageDto} с преобразованными данными
     */
    private MessageDto convertToDto(Message message) {
        MessageDto dto = new MessageDto();
        dto.setId(message.getId());
        dto.setSenderId(message.getSender().getId());
        dto.setSenderName(message.getSender().getNickname());
        dto.setTimestamp(message.getDateOfSending().toString());
        dto.setEncryptedText(message.getEncryptedText());
        dto.setEncryptedAesKeyRecipient(message.getEncryptedAesKeyRecipient());
        dto.setEncryptedAesKeySender(message.getEncryptedAesKeySender());
        dto.setIv(message.getIv());
        return dto;
    }

    /**
     * Возвращает список доступных языков перевода, поддерживаемых системой.
     * Данные получаются из сервиса AppUserService.
     *
     * @return {@link ResponseEntity} со списком карт, содержащих коды и названия языков
     */
    @GetMapping("/api/available-translation-languages")
    @ResponseBody
    public ResponseEntity<List<Map<String, String>>> getAvailableTranslationLanguages() {
        return ResponseEntity.ok(appUserService.getAvailableTranslationLanguages());
    }
}