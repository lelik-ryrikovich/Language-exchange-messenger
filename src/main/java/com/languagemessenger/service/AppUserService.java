package com.languagemessenger.service;

import com.languagemessenger.exception.ValidationException;
import com.languagemessenger.model.*;
import com.languagemessenger.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для управления пользователями, их языками, чатами и сообщениями в Language Messenger.
 * Реализует UserDetailsService для аутентификации, предоставляет методы для регистрации,
 * управления языками (изучение и преподавание), создания чатов и отправки сообщений.
 * Поддерживает транзакции и генерацию RSA-ключей для безопасности.
 */
@Service
@RequiredArgsConstructor
public class AppUserService implements UserDetailsService {
    private final AppUserRepository appUserRepository;
    private final LangToLearnRepository langToLearnRepository;
    private final LangForTeachRepository langForTeachRepository;
    private final LanguageRepository languageRepository;
    private final ProficiencyLevelRepository proficiencyLevelRepository;
    private final CityRepository cityRepository;
    private final PasswordEncoder passwordEncoder;
    private final ChatRepository chatRepository;
    private final ChatMemberRepository chatMemberRepository;
    private final MessageRepository messageRepository;
    private final CountryRepository countryRepository;

    /**
     * Загружает данные пользователя для аутентификации по логину.
     *
     * @param username логин пользователя
     * @return объект UserDetails для Spring Security
     * @throws UsernameNotFoundException если пользователь не найден
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with login: " + username));
        return User.builder()
                .username(appUser.getLogin())
                .password(appUser.getPassword())
                .roles("USER")
                .build();
    }

    /**
     * Возвращает текущего аутентифицированного пользователя.
     *
     * @return объект AppUser текущего пользователя
     * @throws UsernameNotFoundException если пользователь не найден
     */
    public AppUser getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return appUserRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with login: " + username));
    }

    /**
     * Получает список языков, которые пользователь хочет изучать.
     *
     * @param user пользователь
     * @return список объектов LangToLearn
     */
    public List<LangToLearn> getLanguagesToLearn(AppUser user) {
        return langToLearnRepository.findByAppUser(user);
    }

    /**
     * Получает список языков, которые пользователь может преподавать.
     *
     * @param user пользователь
     * @return список объектов LangForTeach
     */
    public List<LangForTeach> getLanguagesToTeach(AppUser user) {
        return langForTeachRepository.findByAppUser(user);
    }

    /**
     * Получает список всех доступных языков.
     *
     * @return список объектов Language
     */
    public List<Language> getAllLanguages() {
        return languageRepository.findAll();
    }

    /**
     * Получает список всех уровней владения языком.
     *
     * @return список объектов ProficiencyLevel
     */
    public List<ProficiencyLevel> getAllProficiencyLevels() {
        return proficiencyLevelRepository.findAll();
    }

    /**
     * Получает список всех стран.
     *
     * @return список объектов Country
     */
    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

    /**
     * Регистрирует нового пользователя с валидацией данных и генерацией RSA-ключей.
     *
     * @param user объект пользователя с данными для регистрации
     * @param cityId идентификатор города пользователя
     * @return сохранённый объект AppUser
     * @throws ValidationException если данные не проходят валидацию
     */
    public AppUser register(AppUser user, Integer cityId) {
        List<String> validationErrors = new ArrayList<>();
        // Валидация уникальности
        if (appUserRepository.findByEmail(user.getEmail()).isPresent()) {
            validationErrors.add("Email already exists");
        }
        if (appUserRepository.findByLogin(user.getLogin()).isPresent()) {
            validationErrors.add("Login already exists");
        }
        if (appUserRepository.findByNickname(user.getNickname()).isPresent()) {
            validationErrors.add("Nickname already exists");
        }
        if (user.getDayOfBirth() == null) {
            validationErrors.add("Date of birth is required");
        } else if (Period.between(user.getDayOfBirth(), LocalDate.now()).getYears() < 13) {
            validationErrors.add("You must be at least 13 years old to register");
        }
        if (user.getPassword().length() < 8) {
            validationErrors.add("Password must be at least 8 characters");
        }

        // Если есть ошибки, выбросить исключение с их списком
        if (!validationErrors.isEmpty()) {
            throw new ValidationException("Validation failed", validationErrors);
        }

        // Хеширование пароля
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Автоматическая установка даты регистрации
        user.setRegistrationDate(LocalDate.now());

        // Установка города
        cityRepository.findById(cityId)
                .ifPresent(user::setCity);

        // Генерация RSA-ключей
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair pair = keyGen.generateKeyPair();
            user.setPublicKey(Base64.getEncoder().encodeToString(pair.getPublic().getEncoded()));
            user.setPrivateKey(Base64.getEncoder().encodeToString(pair.getPrivate().getEncoded()));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to generate RSA keys", e);
        }

        return appUserRepository.save(user);
    }

    /**
     * Добавляет язык для изучения пользователю.
     *
     * @param user пользователь
     * @param languageName название языка
     * @param proficiencyLevelName уровень владения языком
     * @throws IllegalStateException если язык уже добавлен
     * @throws IllegalArgumentException если язык или уровень не найдены
     */
    @Transactional
    public void addLanguageToLearn(AppUser user, String languageName, String proficiencyLevelName) {
        Language language = languageRepository.findById(languageName)
                .orElseThrow(() -> new IllegalArgumentException("Language not found: " + languageName));
        ProficiencyLevel proficiencyLevel = proficiencyLevelRepository.findById(proficiencyLevelName)
                .orElseThrow(() -> new IllegalArgumentException("Proficiency level not found: " + proficiencyLevelName));

        // Проверяем, не добавлен ли язык уже в изучаемые
        if (langToLearnRepository.findByAppUser(user).stream()
                .anyMatch(lang -> lang.getLanguage().getLanguageName().equals(languageName))) {
            throw new IllegalStateException("Language " + languageName + " is already in LangToLearn");
        }

        // Проверяем, не добавлен ли язык уже в преподаваемые
        if (langForTeachRepository.findByAppUser(user).stream()
                .anyMatch(lang -> lang.getLanguage().getLanguageName().equals(languageName))) {
            throw new IllegalStateException("Language " + languageName + " is already in LangToTeach");
        }

        LangToLearn langToLearn = new LangToLearn();
        langToLearn.setAppUser(user);
        langToLearn.setLanguage(language);
        langToLearn.setProficiencyLevel(proficiencyLevel);
        langToLearnRepository.save(langToLearn);
    }

    /**
     * Добавляет язык для преподавания пользователю.
     *
     * @param user пользователь
     * @param languageName название языка
     * @param proficiencyLevelName уровень владения языком
     * @throws IllegalStateException если язык уже добавлен
     * @throws IllegalArgumentException если язык или уровень не найдены
     */
    @Transactional
    public void addLanguageToTeach(AppUser user, String languageName, String proficiencyLevelName) {
        Language language = languageRepository.findById(languageName)
                .orElseThrow(() -> new IllegalArgumentException("Language not found: " + languageName));
        ProficiencyLevel proficiencyLevel = proficiencyLevelRepository.findById(proficiencyLevelName)
                .orElseThrow(() -> new IllegalArgumentException("Proficiency level not found: " + proficiencyLevelName));

        // Проверяем, не добавлен ли язык уже в преподаваемые
        if (langForTeachRepository.findByAppUser(user).stream()
                .anyMatch(lang -> lang.getLanguage().getLanguageName().equals(languageName))) {
            throw new IllegalStateException("Language " + languageName + " is already in LangForTeach");
        }

        // Проверяем, не добавлен ли язык уже в изучаемые
        if (langToLearnRepository.findByAppUser(user).stream()
                .anyMatch(lang -> lang.getLanguage().getLanguageName().equals(languageName))) {
            throw new IllegalStateException("Language " + languageName + " is already in LangForLearn");
        }

        LangForTeach langForTeach = new LangForTeach();
        langForTeach.setAppUser(user);
        langForTeach.setLanguage(language);
        langForTeach.setProficiencyLevel(proficiencyLevel);
        langForTeachRepository.save(langForTeach);
    }

    /**
     * Обновляет уровень владения языком для изучения.
     *
     * @param user пользователь
     * @param languageName название языка
     * @param proficiencyLevelName новый уровень владения языком
     * @throws IllegalStateException если язык не найден в изучаемых
     * @throws IllegalArgumentException если язык или уровень не найдены
     */
    @Transactional
    public void updateLanguageToLearn(AppUser user, String languageName, String proficiencyLevelName) {
        Language language = languageRepository.findById(languageName)
                .orElseThrow(() -> new IllegalArgumentException("Language not found: " + languageName));
        ProficiencyLevel proficiencyLevel = proficiencyLevelRepository.findById(proficiencyLevelName)
                .orElseThrow(() -> new IllegalArgumentException("Proficiency level not found: " + proficiencyLevelName));

        LangToLearn langToLearn = langToLearnRepository.findByAppUser(user).stream()
                .filter(lang -> lang.getLanguage().getLanguageName().equals(languageName))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Language " + languageName + " not found in LangToLearn"));

        langToLearn.setProficiencyLevel(proficiencyLevel);
        langToLearnRepository.save(langToLearn);
    }

    /**
     * Обновляет уровень владения языком для преподавания.
     *
     * @param user пользователь
     * @param languageName название языка
     * @param proficiencyLevelName новый уровень владения языком
     * @throws IllegalStateException если язык не найден в преподаваемых
     * @throws IllegalArgumentException если язык или уровень не найдены
     */
    @Transactional
    public void updateLanguageToTeach(AppUser user, String languageName, String proficiencyLevelName) {
        Language language = languageRepository.findById(languageName)
                .orElseThrow(() -> new IllegalArgumentException("Language not found: " + languageName));
        ProficiencyLevel proficiencyLevel = proficiencyLevelRepository.findById(proficiencyLevelName)
                .orElseThrow(() -> new IllegalArgumentException("Proficiency level not found: " + proficiencyLevelName));

        LangForTeach langForTeach = langForTeachRepository.findByAppUser(user).stream()
                .filter(lang -> lang.getLanguage().getLanguageName().equals(languageName))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Language " + languageName + " not found in LangForTeach"));

        langForTeach.setProficiencyLevel(proficiencyLevel);
        langForTeachRepository.save(langForTeach);
    }

    /**
     * Удаляет язык из списка изучаемых пользователем.
     *
     * @param user пользователь
     * @param languageName название языка
     */
    @Transactional
    public void deleteLanguageToLearn(AppUser user, String languageName) {
        langToLearnRepository.deleteByAppUserAndLanguage_LanguageName(user, languageName);
    }

    /**
     * Удаляет язык из списка преподаваемых пользователем.
     *
     * @param user пользователь
     * @param languageName название языка
     */
    @Transactional
    public void deleteLanguageToTeach(AppUser user, String languageName) {
        langForTeachRepository.deleteByAppUserAndLanguage_LanguageName(user, languageName);
    }

    /**
     * Находит пользователей, соответствующих критериям поиска (язык, уровень, страна).
     *
     * @param currentUser текущий пользователь
     * @param languageName название языка
     * @param proficiencyLevelName уровень владения языком
     * @param country страна (или "Any" для всех)
     * @return список подходящих пользователей
     * @throws IllegalArgumentException если язык, уровень или другие параметры некорректны
     */
    public List<AppUser> findMatchingUsers(AppUser currentUser, String languageName, String proficiencyLevelName, String country) {
        // Валидация
        Language language = languageRepository.findById(languageName)
                .orElseThrow(() -> new IllegalArgumentException("Language not found: " + languageName));
        ProficiencyLevel proficiencyLevel = proficiencyLevelRepository.findById(proficiencyLevelName)
                .orElseThrow(() -> new IllegalArgumentException("Proficiency level not found: " + proficiencyLevelName));

        // Проверяем, что текущий пользователь изучает этот язык
        boolean userLearnsLanguage = langToLearnRepository.findByAppUser(currentUser).stream()
                .anyMatch(lang -> lang.getLanguage().getLanguageName().equals(languageName));
        if (!userLearnsLanguage) {
            throw new IllegalArgumentException("You do not have " + languageName + " in your languages to learn");
        }

        // Находим пользователей, которые преподают указанный язык с указанным уровнем
        List<LangForTeach> teachers = langForTeachRepository.findByLanguage_LanguageNameAndProficiencyLevel_ProficiencyLevelName(
                languageName, proficiencyLevelName);

        // Получаем языки, которые текущий пользователь преподаёт
        List<String> currentUserTeachLanguages = langForTeachRepository.findByAppUser(currentUser).stream()
                .map(lang -> lang.getLanguage().getLanguageName())
                .collect(Collectors.toList());

        // Фильтруем учителей: их LangToLearn должен содержать хотя бы один язык из текущего пользователя LangForTeach
        List<AppUser> matchingUsers = new ArrayList<>();
        for (LangForTeach teacher : teachers) {
            AppUser teacherUser = teacher.getAppUser();
            // Исключаем текущего пользователя
            if (teacherUser.getId().equals(currentUser.getId())) {
                continue;
            }
            // Проверяем, изучает ли учитель хотя бы один язык, который текущий пользователь преподаёт
            boolean hasMutualInterest = langToLearnRepository.findByAppUser(teacherUser).stream()
                    .anyMatch(lang -> currentUserTeachLanguages.contains(lang.getLanguage().getLanguageName()));
            if (!hasMutualInterest) {
                continue;
            }
            // Фильтр по стране
            boolean countryMatches;
            if ("Any".equalsIgnoreCase(country)) {
                countryMatches = true; // При "Any" включаем всех
            } else {
                // Проверяем, есть ли город и страна, и совпадает ли страна
                countryMatches = teacherUser.getCity() != null &&
                        teacherUser.getCity().getCountry() != null &&
                        teacherUser.getCity().getCountry().getCountryName().equals(country);
            }
            if (countryMatches) {
                matchingUsers.add(teacherUser);
            }
        }

        return matchingUsers;
    }

    /**
     * Создаёт новый чат между двумя пользователями.
     *
     * @param user1 первый пользователь
     * @param user2 второй пользователь
     * @return созданный объект Chat
     * @throws IllegalStateException если чат уже существует
     */
    @Transactional
    public Chat createChat(AppUser user1, AppUser user2) {
        // Проверяем, существует ли уже чат между этими пользователями
        List<ChatMember> existingChats = chatMemberRepository.findByAppUser(user1);
        for (ChatMember member : existingChats) {
            boolean hasUser2 = member.getChat().getMembers().stream()
                    .anyMatch(m -> m.getAppUser().getId().equals(user2.getId()));
            if (hasUser2) {
                throw new IllegalStateException("Chat already exists between these users");
            }
        }

        // Создаем новый чат
        Chat chat = new Chat();
        chat.setChatName(user1.getNickname() + " and " + user2.getNickname() + " chat");
        chat = chatRepository.save(chat);

        // Добавляем участников
        ChatMember member1 = new ChatMember();
        member1.setChat(chat);
        member1.setAppUser(user1);
        chatMemberRepository.save(member1);

        ChatMember member2 = new ChatMember();
        member2.setChat(chat);
        member2.setAppUser(user2);
        chatMemberRepository.save(member2);

        return chat;
    }

    /**
     * Получает список чатов пользователя.
     *
     * @param user пользователь
     * @return список объектов Chat
     */
    public List<Chat> getUserChats(AppUser user) {
        return chatMemberRepository.findByAppUser(user).stream()
                .map(ChatMember::getChat)
                .collect(Collectors.toList());
    }

    /**
     * Находит пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @return Optional, содержащий найденного пользователя, или пустой Optional
     */
    public Optional<AppUser> getUserById(Integer id) {
        return appUserRepository.findById(id);
    }

    /**
     * Отправляет сообщение в чат от имени пользователя.
     *
     * @param sender отправитель сообщения
     * @param chatId идентификатор чата
     * @param message объект сообщения
     * @return сохранённый объект Message
     * @throws IllegalArgumentException если чат не найден или пользователь не является участником
     */
    @Transactional
    public Message sendMessage(AppUser sender, Integer chatId, Message message) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found"));

        // Проверяем, что пользователь является участником чата
        boolean isMember = chat.getMembers().stream()
                .anyMatch(member -> member.getAppUser().getId().equals(sender.getId()));
        if (!isMember) {
            throw new IllegalArgumentException("User is not a member of this chat");
        }

        message.setSender(sender);
        message.setChat(chat);
        message.setDateOfSending(ZonedDateTime.now());

        return messageRepository.save(message);
    }

    /**
     * Устанавливает язык перевода для пользователя.
     *
     * @param user пользователь
     * @param languageCode код языка перевода
     * @throws IllegalArgumentException если код языка пустой
     */
    @Transactional
    public void setTranslationLanguage(AppUser user, String languageCode) {
        if (languageCode == null || languageCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Translation language code cannot be empty");
        }
        user.setTranslationLanguage(languageCode);
        appUserRepository.save(user);
    }

    /**
     * Получает текущий язык перевода пользователя.
     *
     * @param user пользователь
     * @return код языка перевода
     */
    public String getTranslationLanguage(AppUser user) {
        return user.getTranslationLanguage();
    }

    /**
     * Получает список доступных языков перевода.
     *
     * @return список map с кодами и названиями языков
     */
    public List<Map<String, String>> getAvailableTranslationLanguages() {
        return List.of(
                Map.of("code", "ru", "name", "Russian"),
                Map.of("code", "en", "name", "English"),
                Map.of("code", "zh", "name", "Chinese"),
                Map.of("code", "es", "name", "Spanish"),
                Map.of("code", "fr", "name", "French"),
                Map.of("code", "de", "name", "German"),
                Map.of("code", "ja", "name", "Japanese"),
                Map.of("code", "ar", "name", "Arabic"),
                Map.of("code", "it", "name", "Italian")
        );
    }

    /**
     * Получает список отображаемых названий уровней владения языком.
     *
     * @return список map с кодами и названиями уровней
     */
    public List<Map<String, String>> getProficiencyLevelDisplayNames() {
        return List.of(
                Map.of("code", "A1", "name", "A1 (Beginner)"),
                Map.of("code", "A2", "name", "A2 (Elementary)"),
                Map.of("code", "B1", "name", "B1 (Intermediate)"),
                Map.of("code", "B2", "name", "B2 (Upper-Intermediate)"),
                Map.of("code", "C1", "name", "C1 (Advanced)"),
                Map.of("code", "C2", "name", "C2 (Proficiency)")
        );
    }
}