package com.palangwi.soup.service.keyword;

import static com.palangwi.soup.utils.KeywordNormalizer.*;

import com.palangwi.soup.domain.keyword.PendingKeywordRequest;
import com.palangwi.soup.domain.keyword.Status;
import com.palangwi.soup.dto.keyword.response.RegisterKeywordResponseDto;
import com.palangwi.soup.exception.keyword.AlreadyRejectedKeywordException;
import com.palangwi.soup.repository.keyword.PendingKeywordRequestRepository;
import com.palangwi.soup.utils.KeywordNormalizer;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.palangwi.soup.domain.keyword.Keyword;
import com.palangwi.soup.domain.keyword.Source;
import com.palangwi.soup.domain.user.User;
import com.palangwi.soup.domain.userkeyword.UserKeyword;
import com.palangwi.soup.dto.keyword.KeywordResponseDto;
import com.palangwi.soup.dto.keyword.RegisterKeywordRequestDto;
import com.palangwi.soup.exception.keyword.AlreadySubscribedKeywordException;
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

    public KeywordResponseDto getKeywordByName(String name) {
        return null;
    }

    public KeywordResponseDto createKeyword(String name) {
        return null;
    }

    public KeywordResponseDto updateKeyword(Long id, String name) {
        return null;
    }

    public void deleteKeyword(Long id) {

    }

    @Transactional
    public RegisterKeywordResponseDto registerKeyword(Long userId,
                                                                      RegisterKeywordRequestDto registerKeywordRequestDto) {
        List<String> keywords = registerKeywordRequestDto.registered();

        List<Keyword> allKeywords = findOrCreateKeywords(keywords, userId);
        User user = findUserById(userId);

        List<UserKeyword> userKeywords = createUserKeywordsIfNotSubscribed(user, allKeywords);
        userKeywordRepository.saveAll(userKeywords);

        return RegisterKeywordResponseDto.of(
                allKeywords.stream()
                        .map(Keyword::getName)
                        .toList());
    }

    private List<Keyword> findOrCreateKeywords(List<String> keywords, Long userId) {
        User user = findUserById(userId);

        List<Keyword> existingKeywords = keywordRepository.findAllByNameIn(keywords);

        Set<String> existingKeywordNames = existingKeywords.stream()
                .map(Keyword::getName)
                .collect(Collectors.toSet());

        List<Keyword> result = new ArrayList<>(existingKeywords);

        for (String name : keywords) {
            if (existingKeywordNames.contains(name)) continue;

            Keyword keyword = handleNonExistingKeyword(name, user);
            result.add(keyword);
        }
        return result;
    }

    private Keyword handleNonExistingKeyword(String name, User user) {
        Optional<Keyword> keywordOpt = keywordRepository.findByName(name);

        if (keywordOpt.isPresent()) {
            Keyword keyword = keywordOpt.get();

            return switch (keyword.getStatus()) {
                case REJECTED -> throw new AlreadyRejectedKeywordException();
                case PENDING -> {
                    handlePendingKeyword(keyword, user);
                    yield keyword;
                }
                default -> keyword;
            };
        }

        return createNewKeyword(name, user);
    }

    private Keyword createNewKeyword(String name, User user) {
        String normalizedName = normalize(name);
        return Keyword.of(name.toLowerCase(), normalizedName, Source.USER_REQUEST, user);
    }

    private void handlePendingKeyword(Keyword keyword, User user) {
        boolean alreadyRequested = keyword.getPendingKeywordRequests().stream()
                .anyMatch(req -> req.getUser().getId().equals(user.getId()));

        if (!alreadyRequested) {
            PendingKeywordRequest request = PendingKeywordRequest.builder()
                    .keyword(keyword)
                    .user(user)
                    .build();
            pendingKeywordRequestRepository.save(request);
        }
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    private List<UserKeyword> createUserKeywordsIfNotSubscribed(User user, List<Keyword> keywords) {
        List<String> alreadySubscribedNames = new ArrayList<>();
        List<UserKeyword> userKeywords = new ArrayList<>();
        for (Keyword keyword : keywords) {
            boolean alreadySubscribed = user.getUserKeywords().isAlreadySubscribed(keyword);
            if (alreadySubscribed) {
                alreadySubscribedNames.add(keyword.getName());
            } else {
                userKeywords.add(UserKeyword.create(user, keyword));
            }
        }
        if (!alreadySubscribedNames.isEmpty()) {
            throw new AlreadySubscribedKeywordException(alreadySubscribedNames);
        }
        return userKeywords;
    }
}