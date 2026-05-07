package com.palangwi.soup.admin.keyword.service;

import com.palangwi.soup.admin.dto.keyword.AddKeywordResponseDto;
import com.palangwi.soup.admin.dto.keyword.AdminKeywordListItemDto;
import com.palangwi.soup.admin.dto.keyword.AdminKeywordListResponseDto;
import com.palangwi.soup.admin.dto.keyword.RemoveKeywordResponseDto;
import com.palangwi.soup.admin.keyword.repository.AdminKeywordRepository;
import com.palangwi.soup.keyword.domain.Keyword;
import com.palangwi.soup.keyword.domain.Status;
import com.palangwi.soup.keyword.exception.KeywordAlreadyExistException;
import com.palangwi.soup.keyword.exception.KeywordInvalidStatusException;
import com.palangwi.soup.keyword.exception.KeywordNotFoundException;
import com.palangwi.soup.keyword.repository.KeywordRepository;
import com.palangwi.soup.subscription.domain.UserKeyword;
import com.palangwi.soup.subscription.repository.UserKeywordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.palangwi.soup.keyword.domain.Source.MANUAL;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminKeywordServiceImpl implements AdminKeywordService {

    private final AdminKeywordRepository adminKeywordRepository;
    private final KeywordRepository keywordRepository;
    private final UserKeywordRepository userKeywordRepository;

    @Transactional(readOnly = true)
    public AdminKeywordListResponseDto getAllKeywordList(String stringStatus, Pageable pageable) {
        Optional<Status> statusOpt = getStatus(stringStatus);

        Page<Keyword> keywordsPage = statusOpt
                .map(status -> keywordRepository.findByStatus(status, pageable))
                .orElseGet(() -> keywordRepository.findAll(pageable));

        List<AdminKeywordListItemDto> result = keywordsPage.getContent().stream()
                .map(AdminKeywordListItemDto::from)
                .toList();

        return new AdminKeywordListResponseDto(
                result,
                keywordsPage.getTotalElements(),
                keywordsPage.getTotalPages(),
                keywordsPage.getNumber() + 1);
    }

    @Transactional
    public AddKeywordResponseDto addKeyword(String keyword) {

        if (keywordRepository.existsByName(keyword)) {
            throw new KeywordAlreadyExistException();
        }

        Keyword newKeyword = Keyword.builder()
                .name(keyword)
                .normalizedName(keyword)
                .source(MANUAL)
                .build();

        keywordRepository.save(newKeyword);

        return AddKeywordResponseDto.of(keyword);
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

    @Transactional
    public RemoveKeywordResponseDto removeKeyword(Long keywordId, String removeReason) {
        LocalDateTime removedAt = LocalDateTime.now();

        Keyword keyword = keywordRepository.findById(keywordId)
                .orElseThrow(KeywordNotFoundException::new);

        if (keyword.getStatus() == Status.DELETED) {
            throw new KeywordInvalidStatusException();
        }

        List<UserKeyword> userKeywordList = userKeywordRepository.findSubscribedByKeywordId(keywordId);

        userKeywordList.forEach(UserKeyword::unsubscribe);

        keyword.remove(removeReason, removedAt);

        return RemoveKeywordResponseDto.of(keyword, removeReason);
    }
}
