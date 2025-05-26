package com.palangwi.soup.repository.userkeyword;

import com.palangwi.soup.domain.userkeyword.UserKeyword;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserKeywordRepository extends JpaRepository<UserKeyword, Long> {

    @Query("SELECT uk FROM UserKeyword uk JOIN FETCH uk.user JOIN FETCH uk.keyword WHERE uk.subscribed = true")
    List<UserKeyword> findAllSubscribedUserKeywords();
}
