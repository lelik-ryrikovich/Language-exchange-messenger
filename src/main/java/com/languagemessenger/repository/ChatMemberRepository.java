package com.languagemessenger.repository;

import com.languagemessenger.model.AppUser;
import com.languagemessenger.model.ChatMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMemberRepository extends JpaRepository<ChatMember, Integer> {
    List<ChatMember> findByAppUser(AppUser appUser);
}
