package com.palangwi.soup.service.admin.keyword;

import static com.palangwi.soup.domain.keyword.Source.MANUAL;

import com.palangwi.soup.domain.BaseEntity;
import com.palangwi.soup.domain.keyword.Keyword;
import com.palangwi.soup.domain.keyword.PendingKeywordRequest;
import com.palangwi.soup.domain.keyword.Status;
import com.palangwi.soup.domain.user.User;
import com.palangwi.soup.domain.userkeyword.UserKeyword;
import com.palangwi.soup.dto.admin.keyword.AddKeywordResponseDto;
import com.palangwi.soup.dto.admin.keyword.AdminKeywordResponseDto;
import com.palangwi.soup.dto.admin.keyword.AdminKeywordResponseListDto;
import com.palangwi.soup.dto.admin.keyword.ApproveKeywordResponseDto;
import com.palangwi.soup.dto.admin.keyword.RejectKeywordResponseDto;
import com.palangwi.soup.dto.admin.keyword.RemoveKeywordResponseDto;
import com.palangwi.soup.dto.keyword.KeywordListResponseDto;
import com.palangwi.soup.dto.keyword.KeywordResponseDto;
import com.palangwi.soup.exception.keyword.AlreadyRejectedKeywordException;
import com.palangwi.soup.exception.keyword.KeywordAlreadyExistException;
import com.palangwi.soup.exception.keyword.KeywordInvalidStatusException;
import com.palangwi.soup.exception.keyword.KeywordNotFoundException;
import com.palangwi.soup.repository.admin.keyword.AdminKeywordRepository;
import com.palangwi.soup.repository.keyword.KeywordRepository;
import com.palangwi.soup.repository.userkeyword.UserKeywordRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminKeywordServiceImpl implements AdminKeywordService {

    private final AdminKeywordRepository adminKeywordRepository;
    private final KeywordRepository keywordRepository;
    private final UserKeywordRepository userKeywordRepository;

    @Transactional(readOnly = true)
    public KeywordListResponseDto getAllKeywordList(String stringStatus, Pageable pageable) {
        Optional<Status> statusOpt = getStatus(stringStatus);

        Page<Keyword> keywordsPage = statusOpt
                .map(status -> keywordRepository.findByStatus(status, pageable))
                .orElseGet(() -> keywordRepository.findAll(pageable));

        List<KeywordResponseDto> result = keywordsPage.getContent().stream()
                .map(KeywordResponseDto::from)
                .toList();

        return new KeywordListResponseDto(
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
