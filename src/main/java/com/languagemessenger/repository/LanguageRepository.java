package com.languagemessenger.repository;

import com.languagemessenger.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LanguageRepository extends JpaRepository<Language, String> {
}
