package com.palangwi.soup.service.keyword;

import static com.palangwi.soup.utils.KeywordNormalizer.*;

import com.palangwi.soup.domain.keyword.PendingKeywordRequest;
import com.palangwi.soup.domain.keyword.Status;
import com.palangwi.soup.dto.keyword.RequestKeywordRequestDto;
import com.palangwi.soup.dto.keyword.SubscribeKeywordRequestDto;
import com.palangwi.soup.dto.keyword.response.RequestKeywordResponseDto;
import com.palangwi.soup.dto.keyword.response.SubscribeKeywordResponseDto;
import com.palangwi.soup.exception.keyword.*;
import com.palangwi.soup.repository.keyword.PendingKeywordRequestRepository;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.palangwi.soup.domain.keyword.Keyword;
import com.palangwi.soup.domain.keyword.Source;
import com.palangwi.soup.domain.user.User;
import com.palangwi.soup.domain.userkeyword.UserKeyword;
import com.palangwi.soup.dto.keyword.KeywordResponseDto;
import com.palangwi.soup.exception.user.UserNotFoundException;
import com.palangwi.soup.repository.keyword.KeywordRepository;
import com.palangwi.soup.repository.userkeyword.UserKeywordRepository;
import com.palangwi.soup.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KeywordServiceImpl implements KeywordService {

    private final KeywordRepository keywordRepository;
    private final UserRepository userRepository;
    private final UserKeywordRepository userKeywordRepository;
    private final PendingKeywordRequestRepository pendingKeywordRequestRepository;

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

    @Transactional
    public RequestKeywordResponseDto requestKeywords(Long userId, RequestKeywordRequestDto requestKeywordRequestDto) {
        User user = findUserById(userId);

        String requestedKeyword = requestKeywordRequestDto.keyword();
        String normalizedKeyword = normalize(requestedKeyword);

        Keyword keyword = findOrCreateKeyword(requestedKeyword, normalizedKeyword, user);

        initPendingKeywordRequest(user, keyword);
        return RequestKeywordResponseDto.of(user, keyword);
    }

    private Keyword findOrCreateKeyword(String requestedKeyword, String normalizedKeyword, User user) {
        if (keywordRepository.existsByNameAndStatus(requestedKeyword, Status.ACTIVE)) {
            throw new KeywordAlreadyRequestedException();
        }

        return keywordRepository.findByNameAndStatus(requestedKeyword, Status.PENDING)
                .orElseGet(() -> {
                    Keyword newKeyword = Keyword.of(requestedKeyword, normalizedKeyword, Source.USER_REQUEST, user);
                    return keywordRepository.save(newKeyword);
                });
    }

    @Transactional
    public SubscribeKeywordResponseDto subscribeKeywords(Long userId,
                                                       SubscribeKeywordRequestDto subscribeKeywordRequestDto) {
        List<String> keywords = subscribeKeywordRequestDto.subscribeKeywords();

        User user = findUserById(userId);
        List<Keyword> allKeywords = validateKeywords(user, keywords);

        List<UserKeyword> userKeywords = createUserKeywordsIfNotSubscribed(user, allKeywords);
        userKeywordRepository.saveAll(userKeywords);

        return SubscribeKeywordResponseDto.of(
                allKeywords.stream()
                        .map(Keyword::getName)
                        .toList());
    }

    private List<Keyword> validateKeywords(User user, List<String> keywords) {
        List<Keyword> existingKeywords = keywordRepository.findAllByNameIn(keywords);

        Map<String, Keyword> keywordMap = existingKeywords.stream()
                .collect(Collectors.toMap(Keyword::getName, k -> k));

        List<String> notFoundKeywords = keywords.stream()
                .filter(name -> !keywordMap.containsKey(name))
                .toList();

        if (!notFoundKeywords.isEmpty()) {
            throw new KeywordNotExistException(notFoundKeywords);
        }

        List<String> alreadySubscribed = keywords.stream()
                .filter(name -> user.getUserKeywords().isAlreadySubscribed(keywordMap.get(name)))
                .toList();

        if (!alreadySubscribed.isEmpty()) {
            throw new AlreadySubscribedKeywordException(alreadySubscribed);
        }

        return keywords.stream()
                .map(keywordMap::get)
                .toList();
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

    private List<UserKeyword> createUserKeywordsIfNotSubscribed(User user, List<Keyword> keywords) {
        List<UserKeyword> userKeywords = new ArrayList<>();
        for (Keyword keyword : keywords) {
            userKeywords.add(UserKeyword.create(user, keyword));
        }
        return userKeywords;
    }
}