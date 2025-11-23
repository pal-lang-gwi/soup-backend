package com.palangwi.soup.admin.dto.keyword;

import com.palangwi.soup.keyword.domain.Keyword;
import com.palangwi.soup.keyword.domain.PendingKeywordRequest;
import com.palangwi.soup.user.domain.User;

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
