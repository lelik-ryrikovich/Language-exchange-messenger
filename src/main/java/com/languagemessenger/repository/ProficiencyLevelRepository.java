package com.languagemessenger.repository;

import com.languagemessenger.model.ProficiencyLevel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProficiencyLevelRepository extends JpaRepository<ProficiencyLevel, String> {
}
