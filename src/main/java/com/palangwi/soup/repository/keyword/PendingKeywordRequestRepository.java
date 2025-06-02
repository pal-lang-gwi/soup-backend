package com.palangwi.soup.repository.keyword;

import com.palangwi.soup.domain.keyword.Keyword;
import com.palangwi.soup.domain.keyword.PendingKeywordRequest;
import com.palangwi.soup.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingKeywordRequestRepository extends JpaRepository<PendingKeywordRequest, Long> {
    boolean existsByUserAndKeyword(User user, Keyword keyword);
}
