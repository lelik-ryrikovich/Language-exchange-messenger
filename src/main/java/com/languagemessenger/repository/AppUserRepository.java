package com.languagemessenger.repository;

import com.languagemessenger.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Integer> {
    Optional<AppUser> findByEmail(String email);
    Optional<AppUser> findByLogin(String login);
    Optional<AppUser> findByNickname(String nickname);
}