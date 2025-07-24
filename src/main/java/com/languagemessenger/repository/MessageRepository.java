// MessageRepository.java
package com.languagemessenger.repository;

import com.languagemessenger.model.Chat;
import com.languagemessenger.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Message, Integer> {
    List<Message> findByChatOrderByDateOfSendingAsc(Chat chat);

    //Optional<Message> findTopByChatOrderByDateOfSendingDesc(Chat chat);
}