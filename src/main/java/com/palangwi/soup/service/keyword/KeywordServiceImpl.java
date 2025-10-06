package com.palangwi.soup.service.keyword;

import static com.palangwi.soup.utils.KeywordNormalizer.normalize;

import com.palangwi.soup.domain.keyword.Keyword;
import com.palangwi.soup.domain.keyword.PendingKeywordRequest;
import com.palangwi.soup.domain.keyword.Source;
import com.palangwi.soup.domain.keyword.Status;
import com.palangwi.soup.domain.user.User;
import com.palangwi.soup.domain.userkeyword.UserKeyword;
import com.palangwi.soup.dto.keyword.KeywordListResponseDto;
import com.palangwi.soup.dto.keyword.KeywordResponseDto;
import com.palangwi.soup.dto.keyword.MyKeywordDto;
import com.palangwi.soup.dto.keyword.MyKeywordListResponseDto;
import com.palangwi.soup.dto.keyword.RequestKeywordRequestDto;
import com.palangwi.soup.dto.keyword.SubscribeKeywordRequestDto;
import com.palangwi.soup.dto.keyword.response.KeywordUnsubscribeResponseDto;
import com.palangwi.soup.dto.keyword.response.RequestKeywordResponseDto;
import com.palangwi.soup.dto.keyword.response.SearchKeywordDto;
import com.palangwi.soup.dto.keyword.response.SearchKeywordsResponseDto;
import com.palangwi.soup.dto.keyword.response.SubscribeKeywordResponseDto;
import com.palangwi.soup.exception.keyword.AlreadySubscribedKeywordException;
import com.palangwi.soup.exception.keyword.KeywordAlreadyRequestedException;
import com.palangwi.soup.exception.keyword.KeywordNotFoundException;
import com.palangwi.soup.exception.keyword.NotSubscribedException;
import com.palangwi.soup.exception.keyword.SubscribedKeywordLimitExceededException;
import com.palangwi.soup.exception.user.UserNotFoundException;
import com.palangwi.soup.repository.keyword.KeywordRepository;
import com.palangwi.soup.repository.keyword.PendingKeywordRequestRepository;
import com.palangwi.soup.repository.user.UserRepository;
import com.palangwi.soup.repository.userkeyword.UserKeywordRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeywordServiceImpl implements KeywordService {

    private final KeywordRepository keywordRepository;
    private final UserRepository userRepository;
    private final UserKeywordRepository userKeywordRepository;
    private final PendingKeywordRequestRepository pendingKeywordRequestRepository;

    private static final int MAXIMUM_KEYWORD_COUNT = 10;

    public KeywordResponseDto findKeywordByName(String name) {
        return null;
    }

    public KeywordResponseDto addKeyword(String name) {
        return null;
    }

    public KeywordResponseDto updateKeywordName(Long id, String name) {
        return null;
    }

    public void deleteKeyword(Long id) {

    }

    @Transactional(readOnly = true)
    public KeywordListResponseDto getKeywordList(Pageable pageable) {
        Page<Keyword> keywordsPage = keywordRepository.findAllByStatus(Status.ACTIVE, pageable);

        List<KeywordResponseDto> result = keywordsPage.getContent().stream()
                .map(KeywordResponseDto::from)
                .toList();
        return new KeywordListResponseDto(
                result,
                keywordsPage.getTotalElements(),
                keywordsPage.getTotalPages(),
                keywordsPage.getNumber() + 1);
    }

    @Transactional(readOnly = true)
    public SearchKeywordsResponseDto searchKeywords(Long userId, String keyword, Pageable pageable) {
        Page<Keyword> keywords = keywordRepository.findByNameContainingIgnoreCaseAndStatus(keyword, Status.ACTIVE,
                pageable);

        List<Keyword> keywordsList = keywords.getContent();
        List<Long> keywordIds = keywordsList.stream()
                .map(Keyword::getId)
                .toList();

        List<Long> subscribedKeywordIds = userKeywordRepository.findSubscribedKeywordIds(userId, keywordIds);

        List<SearchKeywordDto> searchKeywordDtos = keywordsList.stream()
                .map(k -> {
                    boolean isSubscribed = subscribedKeywordIds.contains(k.getId());
                    return SearchKeywordDto.from(k, isSubscribed);
                })
                .toList();

        return SearchKeywordsResponseDto.from(searchKeywordDtos, keywords.getTotalElements(),
                keywords.getTotalPages(), keywords.getNumber() + 1);
    }

    @Transactional
    public RequestKeywordResponseDto requestKeywords(Long userId, RequestKeywordRequestDto requestKeywordRequestDto) {
        User user = findUserById(userId);

        String requestedKeyword = requestKeywordRequestDto.keyword();

        Keyword keyword = findOrCreateKeyword(requestedKeyword, user);

        initPendingKeywordRequest(user, keyword);
        return RequestKeywordResponseDto.of(user, keyword);
    }

    @Transactional
    public KeywordUnsubscribeResponseDto unsubscribeKeyword(Long subscriptionId) {
        UserKeyword userKeyword = userKeywordRepository.findById(subscriptionId)
                .orElseThrow(NotSubscribedException::new);
        userKeyword.unsubscribe();
        return KeywordUnsubscribeResponseDto.of(userKeyword);
    }

    @Transactional(readOnly = true)
    public MyKeywordListResponseDto getMyKeywords(Long userId, Pageable pageable) {
        Page<UserKeyword> userKeywords = userKeywordRepository.findAllByUser_IdAndSubscribedTrue(userId, pageable);

        List<MyKeywordDto> userKeywordList = userKeywords.map(MyKeywordDto::of).getContent();

        return MyKeywordListResponseDto.of(userKeywords, userKeywordList);
    }

    private Keyword findOrCreateKeyword(String requestedKeyword, User user) {
        String normalizedKeyword = normalize(requestedKeyword);

        if (keywordRepository.existsByNameAndStatus(requestedKeyword, Status.ACTIVE)) {
            throw new KeywordAlreadyRequestedException();
        }

        return keywordRepository.findByNameAndStatus(requestedKeyword, Status.PENDING)
                .orElseGet(() -> {
                    Keyword newKeyword = Keyword.of(requestedKeyword, normalizedKeyword, Source.USER_REQUEST, user);
                    // 키워드 저장
                    Keyword savedKeyword = keywordRepository.save(newKeyword);
                    return savedKeyword;
                });
    }


    @Transactional
    public SubscribeKeywordResponseDto subscribeKeyword(Long userId, SubscribeKeywordRequestDto dto) {
        User user = findUserById(userId);
        validateSubscriptionLimit(userId);

        UserKeyword userKeyword = findOrCreateUserKeyword(user, dto.keywordId());
        validateNotAlreadySubscribed(userKeyword);

        userKeyword.subscribe();
        userKeywordRepository.save(userKeyword);

        return toResponseDto(userKeyword);
    }

    private void validateSubscriptionLimit(Long userId) {
        if (userKeywordRepository.countSubscribedKeywordsByUserId(userId) >= MAXIMUM_KEYWORD_COUNT) {
            throw new SubscribedKeywordLimitExceededException();
        }
    }

    private UserKeyword findOrCreateUserKeyword(User user, Long keywordId) {
        return userKeywordRepository.findSubscribedByUserIdAndKeywordId(user.getId(), keywordId)
                .orElseGet(() -> {
                    Keyword keyword = keywordRepository.findById(keywordId)
                            .orElseThrow(KeywordNotFoundException::new);
                    return UserKeyword.create(user, keyword);
                });
    }

    private void validateNotAlreadySubscribed(UserKeyword userKeyword) {
        if (userKeyword.isSubscribed()) {
            throw new AlreadySubscribedKeywordException(userKeyword.getKeyword().getName());
        }
    }

    private SubscribeKeywordResponseDto toResponseDto(UserKeyword userKeyword) {
        Keyword keyword = userKeyword.getKeyword();
        return SubscribeKeywordResponseDto.of(keyword.getId(), keyword.getName());
    }


    private void initPendingKeywordRequest(User user, Keyword keyword) {
        if (pendingKeywordRequestRepository.existsByUserAndKeyword(user, keyword)) {
            throw new KeywordAlreadyRequestedException();
        }

        PendingKeywordRequest request = PendingKeywordRequest.of(user, keyword);
        pendingKeywordRequestRepository.save(request);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }
}