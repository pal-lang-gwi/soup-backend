package com.palangwi.soup.dto.keyword;

import com.palangwi.soup.domain.userkeyword.UserKeyword;
import java.time.LocalDateTime;

public record MyKeywordDto(Long userId, String keyword, String normalizedKeyword, LocalDateTime registeredDate) {
    public static MyKeywordDto of(UserKeyword userKeyword) {
        return new MyKeywordDto(userKeyword.getUser().getId(), userKeyword.getKeyword().getName(), userKeyword.getKeyword().getNormalizedName(), userKeyword.getLastModifiedDate());
    }
}
