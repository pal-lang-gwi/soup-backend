package com.palangwi.soup.dto.admin.keyword;

import com.palangwi.soup.domain.keyword.Keyword;
import com.palangwi.soup.domain.keyword.PendingKeywordRequest;
import com.palangwi.soup.domain.user.User;

public record AdminKeywordResponseDto(
        Long requestId,
        KeywordDto keyword,
        UserDto requestedBy
) {
    public static AdminKeywordResponseDto from(PendingKeywordRequest request) {
        Keyword keyword = request.getKeyword();
        User user = request.getUser();

        return new AdminKeywordResponseDto(
                request.getId(),
                KeywordDto.from(keyword),
                UserDto.from(user)
        );
    }
}
