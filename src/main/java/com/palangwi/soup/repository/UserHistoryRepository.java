package com.palangwi.soup.repository;

import com.palangwi.soup.domain.userlog.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserHistoryRepository extends JpaRepository<UserHistory, Integer> {
    UserHistory findByEmail(String email);
}
