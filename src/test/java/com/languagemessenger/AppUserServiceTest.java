package com.languagemessenger;

import com.languagemessenger.exception.ValidationException;
import com.languagemessenger.model.*;
import com.languagemessenger.repository.*;
import com.languagemessenger.service.AppUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AppUserServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private CityRepository cityRepository;

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private ChatMemberRepository chatMemberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AppUserService appUserService;

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private ProficiencyLevelRepository proficiencyLevelRepository;

    @Mock
    private LangToLearnRepository langToLearnRepository;

    @Mock
    private LangForTeachRepository langForTeachRepository;

    @Mock
    private MessageRepository messageRepository;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void register_WithValidData_Success() {
        AppUser user = new AppUser();
        user.setEmail("new@example.com");
        user.setLogin("newuser");
        user.setNickname("newnick");
        user.setPassword("password123");
        user.setDayOfBirth(LocalDate.of(2000, 1, 1));
        City city = new City();
        city.setId(1);

        when(appUserRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(appUserRepository.findByLogin("newuser")).thenReturn(Optional.empty());
        when(appUserRepository.findByNickname("newnick")).thenReturn(Optional.empty());
        when(cityRepository.findById(1)).thenReturn(Optional.of(city));
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword123");
        when(appUserRepository.save(any(AppUser.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AppUser result = appUserService.register(user, 1);

        assertNotNull(result.getPublicKey());
        assertNotNull(result.getPrivateKey());
        assertEquals("encodedPassword123", result.getPassword());
        verify(appUserRepository).save(user);
    }

    @Test
    void register_WithExistingEmail_ThrowsValidationException() {
        AppUser user = new AppUser();
        user.setEmail("test@example.com");
        user.setLogin("newuser");
        user.setNickname("newnick");
        user.setPassword("password123");
        user.setDayOfBirth(LocalDate.of(2000, 1, 1));

        when(appUserRepository.findByEmail("test@example.com")).thenReturn(Optional.of(new AppUser()));

        ValidationException exception = assertThrows(ValidationException.class, () -> appUserService.register(user, 1));
        assertTrue(exception.getErrors().contains("Email already exists"));
    }

    @Test
    void register_WithPasswordTooShort_ThrowsValidationException() {
        AppUser user = new AppUser();
        user.setEmail("new@example.com");
        user.setLogin("newuser");
        user.setNickname("newnick");
        user.setPassword("pass");
        user.setDayOfBirth(LocalDate.of(2000, 1, 1));

        when(appUserRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(appUserRepository.findByLogin("newuser")).thenReturn(Optional.empty());
        when(appUserRepository.findByNickname("newnick")).thenReturn(Optional.empty());

        ValidationException exception = assertThrows(ValidationException.class, () -> appUserService.register(user, 1));
        assertTrue(exception.getErrors().contains("Password must be at least 8 characters"));
    }

    @Test
    void createChat_WithValidUsers_Success() {
        AppUser user1 = new AppUser();
        user1.setId(1);
        user1.setNickname("User1");
        AppUser user2 = new AppUser();
        user2.setId(2);
        user2.setNickname("User2");

        when(chatRepository.save(any(Chat.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(chatMemberRepository.save(any(ChatMember.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Chat result = appUserService.createChat(user1, user2);

        assertNotNull(result.getChatName());
        assertEquals("User1 and User2 chat", result.getChatName());
        verify(chatRepository).save(any(Chat.class));
        verify(chatMemberRepository, times(2)).save(any(ChatMember.class));
    }

    @Test
    void createChat_WithExistingChat_ThrowsException() {
        // Настраиваем мок для имитации существующего чата
        AppUser user1 = new AppUser();
        user1.setId(1);
        AppUser user2 = new AppUser();
        user2.setId(2);
        Chat existingChat = new Chat();
        ChatMember member1 = new ChatMember();
        member1.setChat(existingChat);
        member1.setAppUser(user1);
        ChatMember member2 = new ChatMember();
        member2.setChat(existingChat);
        member2.setAppUser(user2);
        existingChat.setMembers(Collections.singletonList(member2)); // Добавляем member2 для проверки

        // Настраиваем мок для chatMemberRepository
        when(chatMemberRepository.findByAppUser(user1)).thenReturn(Collections.singletonList(member1));
        when(chatRepository.findById(anyInt())).thenReturn(Optional.of(existingChat));

        // Проверяем, что при попытке создать чат выбрасывается исключение
        assertThrows(IllegalStateException.class, () -> appUserService.createChat(user1, user2));
    }

    @Test
    void addLanguageToLearn_WithValidData_Success() {
        AppUser user = new AppUser();
        user.setId(1);
        Language language = new Language();
        language.setLanguageName("English");
        ProficiencyLevel proficiencyLevel = new ProficiencyLevel();
        proficiencyLevel.setProficiencyLevelName("A1");

        when(languageRepository.findById("English")).thenReturn(Optional.of(language));
        when(proficiencyLevelRepository.findById("A1")).thenReturn(Optional.of(proficiencyLevel));
        when(langToLearnRepository.findByAppUser(user)).thenReturn(Collections.emptyList());
        when(langForTeachRepository.findByAppUser(user)).thenReturn(Collections.emptyList());
        when(langToLearnRepository.save(any(LangToLearn.class))).thenAnswer(invocation -> invocation.getArgument(0));

        appUserService.addLanguageToLearn(user, "English", "A1");

        verify(langToLearnRepository).save(any(LangToLearn.class));
    }

    @Test
    void addLanguageToLearn_WithSameLanguageInTeach_ThrowsException() {
        AppUser user = new AppUser();
        user.setId(1);
        Language language = new Language();
        language.setLanguageName("French");
        ProficiencyLevel proficiencyLevel = new ProficiencyLevel();
        proficiencyLevel.setProficiencyLevelName("A2");
        LangForTeach existingTeach = new LangForTeach();
        existingTeach.setLanguage(language);

        when(languageRepository.findById("French")).thenReturn(Optional.of(language));
        when(proficiencyLevelRepository.findById("A2")).thenReturn(Optional.of(proficiencyLevel));
        when(langToLearnRepository.findByAppUser(user)).thenReturn(Collections.emptyList());
        when(langForTeachRepository.findByAppUser(user)).thenReturn(Collections.singletonList(existingTeach));

        assertThrows(IllegalStateException.class, () -> appUserService.addLanguageToLearn(user, "French", "A2"));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> appUserService.addLanguageToLearn(user, "French", "A2"));
        System.out.println("Exception message: " + exception.getMessage());
        assertEquals("Language French is already in LangToTeach", exception.getMessage());
    }

    @Test
    void addLanguageForTeach_WithSameLanguageInLearn_ThrowsException() {
        AppUser user = new AppUser();
        user.setId(1);

        Language language = new Language();
        language.setLanguageName("French");

        ProficiencyLevel proficiencyLevel = new ProficiencyLevel();
        proficiencyLevel.setProficiencyLevelName("A2");

        LangToLearn existingLearn = new LangToLearn();
        existingLearn.setLanguage(language);

        when(languageRepository.findById("French")).thenReturn(Optional.of(language));
        when(proficiencyLevelRepository.findById("A2")).thenReturn(Optional.of(proficiencyLevel));
        when(langForTeachRepository.findByAppUser(user)).thenReturn(Collections.emptyList());
        when(langToLearnRepository.findByAppUser(user)).thenReturn(Collections.singletonList(existingLearn));

        assertThrows(IllegalStateException.class, () -> appUserService.addLanguageToTeach(user, "French", "A2"));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> appUserService.addLanguageToTeach(user, "French", "A2"));
        System.out.println("Exception message: " + exception.getMessage());
        assertEquals("Language French is already in LangForLearn", exception.getMessage());
    }

    @Test
    void sendMessage_WithValidData_Success() {
        AppUser sender = new AppUser();
        sender.setId(1);
        Chat chat = new Chat();
        chat.setId(1);
        ChatMember member = new ChatMember();
        member.setAppUser(sender);
        chat.setMembers(Collections.singletonList(member));
        Message message = new Message();
        message.setEncryptedText("Test message");

        when(chatRepository.findById(1)).thenReturn(Optional.of(chat));
        when(messageRepository.save(any(Message.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Message result = appUserService.sendMessage(sender, 1, message);

        assertNotNull(result.getDateOfSending());
        assertEquals(sender, result.getSender());
        assertEquals(chat, result.getChat());
        verify(messageRepository).save(message);
    }

    @Test
    void sendMessage_WithNonMember_ThrowsException() {
        AppUser sender = new AppUser();
        sender.setId(1); // Отправитель с id=1
        AppUser otherUser = new AppUser();
        otherUser.setId(2); // Другой пользователь с id=2
        Chat chat = new Chat();
        chat.setId(1);
        ChatMember member = new ChatMember();
        member.setAppUser(otherUser); // Устанавливаем другого пользователя
        chat.setMembers(Collections.singletonList(member)); // Чат содержит другого пользователя

        when(chatRepository.findById(1)).thenReturn(Optional.of(chat));

        assertThrows(IllegalArgumentException.class, () -> appUserService.sendMessage(sender, 1, new Message()));
        verify(messageRepository, never()).save(any(Message.class));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> appUserService.sendMessage(sender, 1, new Message()));
        System.out.println("Exception message: " + exception.getMessage());
    }

    @Test
    void addLanguageToLearn_WithDuplicateLanguage_ThrowsException() {
        AppUser user = new AppUser();
        user.setId(1);
        Language language = new Language();
        language.setLanguageName("English");
        ProficiencyLevel proficiencyLevel = new ProficiencyLevel();
        proficiencyLevel.setProficiencyLevelName("A1");
        LangToLearn existingLearn = new LangToLearn();
        existingLearn.setLanguage(language);

        when(languageRepository.findById("English")).thenReturn(Optional.of(language));
        when(proficiencyLevelRepository.findById("A1")).thenReturn(Optional.of(proficiencyLevel));
        when(langToLearnRepository.findByAppUser(user)).thenReturn(Collections.singletonList(existingLearn));
        when(langForTeachRepository.findByAppUser(user)).thenReturn(Collections.emptyList());

        assertThrows(IllegalStateException.class, () -> appUserService.addLanguageToLearn(user, "English", "A1"));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> appUserService.addLanguageToLearn(user, "English", "A1"));
        System.out.println("Exception message: " + exception.getMessage());
        assertEquals("Language English is already in LangToLearn", exception.getMessage());
    }

    @Test
    void addLanguageToTeach_WithDuplicateLanguage_ThrowsException() {
        AppUser user = new AppUser();
        user.setId(1);

        Language language = new Language();
        language.setLanguageName("English");

        ProficiencyLevel proficiencyLevel = new ProficiencyLevel();
        proficiencyLevel.setProficiencyLevelName("C1");

        LangForTeach existingTeach = new LangForTeach();
        existingTeach.setLanguage(language);

        when(languageRepository.findById("English")).thenReturn(Optional.of(language));
        when(proficiencyLevelRepository.findById("C1")).thenReturn(Optional.of(proficiencyLevel));
        when(langToLearnRepository.findByAppUser(user)).thenReturn(Collections.emptyList());
        when(langForTeachRepository.findByAppUser(user)).thenReturn(Collections.singletonList(existingTeach));

        assertThrows(IllegalStateException.class, () -> appUserService.addLanguageToTeach(user, "English", "C1"));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> appUserService.addLanguageToTeach(user, "English", "C1"));
        System.out.println("Exception message: " + exception.getMessage());
        assertEquals("Language English is already in LangForTeach", exception.getMessage());
    }

}