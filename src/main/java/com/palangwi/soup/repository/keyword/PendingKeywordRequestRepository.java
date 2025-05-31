package com.palangwi.soup.repository.keyword;

import com.palangwi.soup.domain.keyword.PendingKeywordRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PendingKeywordRequestRepository extends JpaRepository<PendingKeywordRequest, Long> {
}
