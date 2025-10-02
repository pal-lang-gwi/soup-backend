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
import com.palangwi.soup.exception.keyword.*;
import com.palangwi.soup.exception.user.UserNotFoundException;
import com.palangwi.soup.repository.keyword.KeywordRepository;
import com.palangwi.soup.repository.keyword.PendingKeywordRequestRepository;
import com.palangwi.soup.repository.user.UserRepository;
import com.palangwi.soup.repository.userkeyword.UserKeywordRepository;
import com.palangwi.soup.service.embedding.EmbeddingService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
    private final EmbeddingService embeddingService;

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
    public KeywordUnsubscribeResponseDto unsubscribeKeyword(Long userId, Long keywordId) {
        UserKeyword userKeyword = userKeywordRepository.findSubscribedByUserIdAndKeywordId(userId, keywordId)
                .orElseThrow(NotSubscribedException::new);
        userKeyword.unsubscribe();
        return KeywordUnsubscribeResponseDto.of(userKeyword);
    }

    @Transactional(readOnly = true)
    public MyKeywordListResponseDto getMyKeywords(Long userId, Pageable pageable) {
        Page<UserKeyword> userKeywords = userKeywordRepository.findAllByUser_IdAndSubscribedTrue(userId, pageable);

        List<MyKeywordDto> userKeywordList = userKeywords.getContent().stream()
                .map(MyKeywordDto::of)
                .toList();

        return MyKeywordListResponseDto.of(userKeywordList, userKeywords);
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

                    // 임베딩 생성 (비동기)
                    embeddingService.generateEmbedding(requestedKeyword)
                            .thenAccept(embedding -> updateKeywordEmbedding(savedKeyword.getId(), embedding))
                            .exceptionally(ex -> {
                                log.error("❌ 키워드 임베딩 생성 실패: {}", requestedKeyword, ex);
                                return null;
                            });

                    return savedKeyword;
                });
    }

    @Transactional
    public void updateKeywordEmbedding(Long keywordId, float[] embedding) {
        Keyword keyword = keywordRepository.findById(keywordId)
                .orElseThrow(() -> new KeywordNotFoundException());
        keyword.setEmbedding(embedding);
        log.info("✅ 키워드 임베딩 업데이트 완료: keywordId={}, 차원={}", keywordId, embedding.length);
    }

    @Transactional
    public SubscribeKeywordResponseDto subscribeKeywords(Long userId,
            SubscribeKeywordRequestDto subscribeKeywordRequestDto) {
        User user = findUserById(userId);

        if (userKeywordRepository.countSubscribedKeywordsByUserId(userId) > MAXIMUM_KEYWORD_COUNT) {
            throw new SubscribedKeywordLimitExceededException();
        }

        List<String> keywordNames = subscribeKeywordRequestDto.subscribeKeywords();

        List<UserKeyword> userKeywords = createUserKeywords(user, keywordNames);
        userKeywordRepository.saveAll(userKeywords);

        return SubscribeKeywordResponseDto.of(
                userKeywords.stream()
                        .map(userKeyword -> userKeyword.getKeyword().getName())
                        .toList());
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

    private List<UserKeyword> createUserKeywords(User user, List<String> keywordNames) {
        Map<String, Keyword> keywordMap = validateKeywordNames(user, keywordNames);
        return filterAndBuildUserKeywords(user, keywordNames, keywordMap);
    }

    private Map<String, Keyword> validateKeywordNames(User user, List<String> keywordNames) {
        List<Keyword> existingKeywords = keywordRepository.findAllByNameIn(keywordNames);
        Map<String, Keyword> keywordMap = existingKeywords.stream()
                .collect(Collectors.toMap(Keyword::getName, k -> k));

        List<String> notFound = keywordNames.stream()
                .filter(name -> !keywordMap.containsKey(name))
                .toList();

        if (!notFound.isEmpty()) {
            log.warn("{} 사용자가 요청한 다음 키워드들을 찾을 수 없습니다: {}", user.getId(), notFound);
            throw new KeywordNotExistException(notFound);
        }

        return keywordMap;
    }

    private List<UserKeyword> filterAndBuildUserKeywords(User user, List<String> keywordNames,
            Map<String, Keyword> keywordMap) {
        List<UserKeyword> result = new ArrayList<>();
        List<String> alreadySubscribed = new ArrayList<>();

        for (String name : keywordNames) {
            Keyword keyword = keywordMap.get(name);

            user.getUserKeywords().findByKeyword(keyword).ifPresentOrElse(
                    userKeyword -> {
                        if (userKeyword.isSubscribed()) {
                            alreadySubscribed.add(name);
                        } else {
                            userKeyword.subscribe();
                            result.add(userKeyword);
                        }
                    },
                    () -> result.add(UserKeyword.create(user, keyword)));
        }

        if (!alreadySubscribed.isEmpty()) {
            throw new AlreadySubscribedKeywordException(alreadySubscribed);
        }

        return result;
    }
}