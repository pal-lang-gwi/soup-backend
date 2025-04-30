package com.palangwi.soup.repository;

import com.palangwi.soup.domain.userlog.ChangeType;
import com.palangwi.soup.domain.userlog.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserHistoryRepository extends JpaRepository<UserHistory, Integer> {
    Optional<UserHistory> findByEmail(String email);

    Optional<UserHistory> findTopByEmailAndChangeTypeOrderByCreatedDateDesc(String email, ChangeType changeType);
}
