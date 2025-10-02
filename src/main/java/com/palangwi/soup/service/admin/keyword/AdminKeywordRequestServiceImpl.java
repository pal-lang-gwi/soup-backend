package com.palangwi.soup.service.admin.keyword;

import com.palangwi.soup.domain.BaseEntity;
import com.palangwi.soup.domain.keyword.Keyword;
import com.palangwi.soup.domain.keyword.PendingKeywordRequest;
import com.palangwi.soup.domain.keyword.Status;
import com.palangwi.soup.domain.user.User;
import com.palangwi.soup.domain.userkeyword.UserKeyword;
import com.palangwi.soup.dto.admin.keyword.AdminKeywordResponseDto;
import com.palangwi.soup.dto.admin.keyword.AdminKeywordResponseListDto;
import com.palangwi.soup.dto.admin.keyword.ApproveKeywordResponseDto;
import com.palangwi.soup.dto.admin.keyword.RejectKeywordResponseDto;
import com.palangwi.soup.exception.keyword.AlreadyRejectedKeywordException;
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
public class AdminKeywordRequestServiceImpl implements AdminKeywordRequestService {

    private final AdminKeywordRepository adminKeywordRepository;
    private final KeywordRepository keywordRepository;
    private final UserKeywordRepository userKeywordRepository;

    @Transactional(readOnly = true)
    public AdminKeywordResponseListDto getRequestedKeywords(String stringStatus, Pageable pageable) {
        Optional<Status> statusOpt = getStatus(stringStatus);

        Page<PendingKeywordRequest> page = statusOpt
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
    public ApproveKeywordResponseDto approveKeyword(Long requestId) {
        PendingKeywordRequest request = adminKeywordRepository.findById(requestId)
                .orElseThrow(KeywordNotFoundException::new);

        Keyword keyword = request.getKeyword();
        keyword.approve(request.getUser());

        List<UserKeyword> userKeywords = keyword.getPendingKeywordRequests().stream()
                .map(PendingKeywordRequest::getUser)
                .filter(user -> !userKeywordRepository.existsByUser_IdAndKeyword_Id(user.getId(), keyword.getId()))
                .map(user -> {
                    UserKeyword uk = UserKeyword.create(user, keyword);
                    uk.subscribe();
                    return uk;
                })
                .toList();

        userKeywordRepository.saveAll(userKeywords);

        adminKeywordRepository.deleteAllByKeyword(keyword);

        return ApproveKeywordResponseDto.of(keyword.getName(), userKeywords.size());
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

    @Transactional
    public RejectKeywordResponseDto rejectKeyword(Long requestId, String rejectReason) {
        LocalDateTime rejectedAt = LocalDateTime.now();

        PendingKeywordRequest request = adminKeywordRepository.findById(requestId)
                .orElseThrow(KeywordNotFoundException::new);

        Keyword keyword = request.getKeyword();

        if (keyword.getStatus() == Status.REJECTED) {
            throw new AlreadyRejectedKeywordException();
        }

        keyword.reject(rejectReason, rejectedAt);

        adminKeywordRepository.deleteByKeywordId(keyword.getId());

        keyword.getPendingKeywordRequests().clear();

        return RejectKeywordResponseDto.of(keyword, rejectReason);
    }
}