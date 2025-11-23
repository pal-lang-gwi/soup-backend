package com.palangwi.soup.admin.keyword.repository;

import com.palangwi.soup.keyword.domain.Keyword;
import com.palangwi.soup.keyword.domain.PendingKeywordRequest;
import com.palangwi.soup.keyword.domain.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminKeywordRepository extends JpaRepository<PendingKeywordRequest, Long> {
    @Query("SELECT p FROM PendingKeywordRequest p WHERE p.keyword.status = :status")
    Page<PendingKeywordRequest> findByStatus(@Param("status") Status status, Pageable pageable);

    void deleteByKeywordId(Long id);
    void deleteAllByKeyword(Keyword keyword);
}
