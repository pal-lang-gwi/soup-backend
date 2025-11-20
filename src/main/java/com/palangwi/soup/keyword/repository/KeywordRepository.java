package com.palangwi.soup.keyword.repository;

import com.palangwi.soup.keyword.domain.Keyword;
import com.palangwi.soup.keyword.domain.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    boolean existsByName(String name);

    boolean existsByNameAndStatus(String name, Status status);

    Optional<Keyword> findByName(String name);

    Optional<Keyword> findByNameAndStatus(String name, Status status);

    List<Keyword> findAllByNameIn(List<String> names);

    List<Keyword> findAllByStatus(Status status);

    Page<Keyword> findByNameContainingIgnoreCaseAndStatus(String name, Status status, Pageable pageable);

    Page<Keyword> findAllByStatus(Status status, Pageable pageable);

    @Query("SELECT k FROM Keyword k WHERE k.status = :status")
    Page<Keyword> findByStatus(@Param("status") Status status, Pageable pageable);

}