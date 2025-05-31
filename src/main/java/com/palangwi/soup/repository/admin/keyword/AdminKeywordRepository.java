package com.palangwi.soup.repository.admin.keyword;

import com.palangwi.soup.domain.keyword.Keyword;
import com.palangwi.soup.domain.keyword.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminKeywordRepository extends JpaRepository<Keyword, Long> {
    Page<Keyword> findByStatus(Status status, Pageable pageable);
}
