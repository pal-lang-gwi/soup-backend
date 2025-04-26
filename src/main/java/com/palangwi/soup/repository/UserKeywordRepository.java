package com.palangwi.soup.repository;

import com.palangwi.soup.domain.userkeyword.UserKeyword;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserKeywordRepository extends JpaRepository<UserKeyword, Long> {

    @Query("SELECT uk FROM UserKeyword uk JOIN uk.user u WHERE uk.subscribed = true AND u.deleted = false")
    List<UserKeyword> findAllSubscribedUserKeywords();
}
