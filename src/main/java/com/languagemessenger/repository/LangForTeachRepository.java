package com.languagemessenger.repository;

import com.languagemessenger.model.AppUser;
import com.languagemessenger.model.LangForTeach;
import com.languagemessenger.model.LangForTeachId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LangForTeachRepository extends JpaRepository<LangForTeach, LangForTeachId> {
    List<LangForTeach> findByAppUser(AppUser appUser);
    void deleteByAppUserAndLanguage_LanguageName(AppUser appUser, String languageName);

    List<LangForTeach> findByLanguage_LanguageNameAndProficiencyLevel_ProficiencyLevelName(
            String languageName, String proficiencyLevelName);
}
