package com.palangwi.soup.repository.keyword;

import com.palangwi.soup.domain.keyword.Status;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.palangwi.soup.domain.keyword.Keyword;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
    
    boolean existsByName(String name);

    boolean existsByNameAndStatus(String name, Status status);

    Optional<Keyword> findByName(String name);

    List<Keyword> findAllByNameIn(List<String> names);

    Optional<Keyword> findByNameAndStatus(String name, Status status);
}