package com.palangwi.soup.dto.admin.keyword;

import com.palangwi.soup.domain.BaseEntity;
import com.palangwi.soup.domain.keyword.Keyword;
import com.palangwi.soup.domain.keyword.PendingKeywordRequest;
import com.palangwi.soup.domain.user.User;
import com.palangwi.soup.exception.user.UserNotFoundException;
import java.util.Comparator;

public record AdminKeywordResponseDto(
        Long requestId,
        KeywordDto keyword,
        UserDto requestedBy
) {
    public static AdminKeywordResponseDto from(PendingKeywordRequest request) {
        Keyword keyword = request.getKeyword();
        User user = keyword.getPendingKeywordRequests().stream()
                .sorted(Comparator.comparing(BaseEntity::getCreatedDate))
                .map(PendingKeywordRequest::getUser)
                .findFirst()
                .orElseThrow(UserNotFoundException::new);

        return new AdminKeywordResponseDto(
                request.getId(),
                KeywordDto.from(keyword),
                UserDto.from(user)
        );
    }
}
