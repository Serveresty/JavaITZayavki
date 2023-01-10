package com.Obrabotka.IT.repository;

import com.Obrabotka.IT.models.Theme;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ThemeRepository extends JpaRepository<Theme, Long> {
    Theme findByTitle(String name);
}
