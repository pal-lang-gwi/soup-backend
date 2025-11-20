package com.palangwi.soup.keyword.repository;

import com.palangwi.soup.keyword.domain.Keyword;
import com.palangwi.soup.keyword.domain.PendingKeywordRequest;
import com.palangwi.soup.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingKeywordRequestRepository extends JpaRepository<PendingKeywordRequest, Long> {
    boolean existsByUserAndKeyword(User user, Keyword keyword);
}
