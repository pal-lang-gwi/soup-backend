package com.palangwi.soup.service.admin.keyword;

import com.palangwi.soup.domain.BaseEntity;
import com.palangwi.soup.domain.keyword.Keyword;
import com.palangwi.soup.domain.keyword.PendingKeywordRequest;
import com.palangwi.soup.domain.keyword.Status;
import com.palangwi.soup.domain.user.User;
import com.palangwi.soup.dto.admin.keyword.AdminKeywordResponseDto;
import com.palangwi.soup.dto.admin.keyword.AdminKeywordResponseListDto;
import com.palangwi.soup.exception.keyword.KeywordNotFoundException;
import com.palangwi.soup.repository.admin.keyword.AdminKeywordRepository;
import com.palangwi.soup.repository.keyword.KeywordRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminKeywordService {

    private final AdminKeywordRepository adminKeywordRepository;
    private final KeywordRepository keywordRepository;

    @Transactional(readOnly = true)
    public AdminKeywordResponseListDto getRequestedKeywords(String stringStatus, Pageable pageable) {
        Page<PendingKeywordRequest> page = getStatus(stringStatus)
                .map(status -> adminKeywordRepository.findByStatus(status, pageable))
                .orElseGet(() -> adminKeywordRepository.findAll(pageable));

        List<AdminKeywordResponseDto> content = page.getContent().stream()
                .map(AdminKeywordResponseDto::from)
                .toList();

        return new AdminKeywordResponseListDto(
                content,
                page.getTotalPages(),
                page.getTotalElements(),
                page.getNumber()
        );
    }

    @Transactional
    public void approveKeyword(Long requestId) {
        PendingKeywordRequest request = adminKeywordRepository.findById(requestId)
                .orElseThrow(KeywordNotFoundException::new);

        Keyword keyword = keywordRepository.findByName(request.getKeyword().getName())
                .orElseThrow(KeywordNotFoundException::new);

        User firstRequestUser = getFirstRequestUser(keyword);

        keyword.approve(firstRequestUser);
    }

    private User getFirstRequestUser(Keyword keyword) {
        return keyword.getPendingKeywordRequests().stream()
                .sorted(Comparator.comparing(BaseEntity::getCreatedDate))
                .findFirst()
                .map(PendingKeywordRequest::getUser)
                .orElseThrow(KeywordNotFoundException::new);
    }

    private Optional<Status> getStatus(String stringStatus) {
        try {
            return Optional.ofNullable(stringStatus)
                    .map(String::toUpperCase)
                    .map(Status::valueOf);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
