package com.palangwi.soup.service.keyword;

import com.palangwi.soup.dto.keyword.response.RegisterKeywordResponseDto;
import java.util.ArrayList;
import java.util.List;
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

        List<Keyword> allKeywords = findOrCreateKeywords(keywords);
        User user = findUserById(userId);

        List<UserKeyword> userKeywords = createUserKeywordsIfNotSubscribed(user, allKeywords);
        userKeywordRepository.saveAll(userKeywords);

        return RegisterKeywordResponseDto.of(
                allKeywords.stream()
                        .map(Keyword::getName)
                        .toList());
    }

    private List<Keyword> findOrCreateKeywords(List<String> keywords) {
        List<Keyword> existingKeywords = keywordRepository.findAllByNameIn(keywords);
        Set<String> existingKeywordNames = existingKeywords.stream()
                .map(Keyword::getName)
                .collect(Collectors.toSet());

        List<Keyword> newKeywords = keywords.stream()
                .filter(name -> !existingKeywordNames.contains(name.toLowerCase()))
                // TODO : Keyword의 nomalizedName을 어떻게 설정할지 논의 필요
                .map(name -> Keyword.of(name.toLowerCase(), name, Source.USER_REQUEST))
                .toList();
        keywordRepository.saveAll(newKeywords);

        List<Keyword> allKeywords = new ArrayList<>(existingKeywords);
        allKeywords.addAll(newKeywords);
        return allKeywords;
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