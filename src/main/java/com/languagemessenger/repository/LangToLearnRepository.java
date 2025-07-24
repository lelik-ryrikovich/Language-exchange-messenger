package com.languagemessenger.repository;

import com.languagemessenger.model.AppUser;
import com.languagemessenger.model.LangToLearn;
import com.languagemessenger.model.LangToLearnId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LangToLearnRepository extends JpaRepository<LangToLearn, LangToLearnId> {
    List<LangToLearn> findByAppUser(AppUser appUser);
    void deleteByAppUserAndLanguage_LanguageName(AppUser appUser, String languageName);
    List<LangToLearn> findByLanguage_LanguageName(String languageName);
}
