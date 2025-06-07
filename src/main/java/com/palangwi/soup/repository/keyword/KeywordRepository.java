package com.palangwi.soup.repository.keyword;

import com.palangwi.soup.domain.keyword.Status;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.palangwi.soup.domain.keyword.Keyword;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    boolean existsByName(String name);

    boolean existsByNameAndStatus(String name, Status status);

    Optional<Keyword> findByName(String name);

    List<Keyword> findAllByNameIn(List<String> names);

    Optional<Keyword> findByNameAndStatus(String name, Status status);

    List<Keyword> findByNameContainingIgnoreCaseAndStatus(String name, Status status);

    Page<Keyword> findAllByStatus(Status status, Pageable pageable);

    @Query("SELECT k, CASE WHEN uk.subscribed = true THEN true ELSE false END " +
            "FROM Keyword k " +
            "LEFT JOIN UserKeyword uk ON uk.keyword = k AND uk.user.id = :userId " +
            "WHERE k.name LIKE %:keyword% AND k.status = :status")
    List<Object[]> findKeywordsWithSubscriptionStatus(
            @Param("userId") Long userId,
            @Param("keyword") String keyword,
            @Param("status") Status status);
}