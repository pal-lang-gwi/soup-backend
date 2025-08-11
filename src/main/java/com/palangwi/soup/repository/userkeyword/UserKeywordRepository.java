package com.palangwi.soup.repository.userkeyword;

import com.palangwi.soup.domain.userkeyword.UserKeyword;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserKeywordRepository extends JpaRepository<UserKeyword, Long> {

    @Query("SELECT uk FROM UserKeyword uk JOIN FETCH uk.user JOIN FETCH uk.keyword WHERE uk.subscribed = true")
    List<UserKeyword> findAllSubscribedUserKeywords();

    @Query("SELECT COUNT(DISTINCT uk.user) FROM UserKeyword uk WHERE uk.subscribed = true")
    int countDistinctSubscribedUsers();

    @Query("SELECT uk FROM UserKeyword uk WHERE uk.user.id = :userId AND uk.keyword.id = :keywordId AND uk.subscribed = true")
    Optional<UserKeyword> findSubscribedByUserIdAndKeywordId(@Param("userId") Long userId,
                                                             @Param("keywordId") Long keywordId);

    @Query("SELECT uk FROM UserKeyword uk WHERE uk.keyword.id = :keywordId AND uk.subscribed = true")
    List<UserKeyword> findSubscribedByKeywordId(@Param("keywordId") Long keywordId);

    Page<UserKeyword> findAllByUser_IdAndSubscribedTrue(Long userId, Pageable pageable);

    @Query("SELECT COUNT(*) FROM UserKeyword uk WHERE uk.user.id = :userId AND uk.subscribed = true")
    Integer countSubscribedKeywordsByUserId(@Param("userId") Long userId);

    @Query("""
              SELECT uk.keyword.id
              FROM UserKeyword uk
              WHERE uk.user.id = :userId
                AND uk.subscribed = true
                AND uk.keyword.id IN :keywordIds
            """)
    List<Long> findSubscribedKeywordIds(
            @Param("userId") Long userId,
            @Param("keywordIds") List<Long> keywordIds
    );
}
