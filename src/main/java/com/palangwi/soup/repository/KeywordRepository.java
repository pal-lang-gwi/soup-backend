package com.palangwi.soup.repository;

import com.palangwi.soup.domain.keyword.Keyword;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    boolean existsByName(String name);

    Optional<Keyword> findByName(String name);
}