package com.palangwi.soup.subscription.domain;

import com.palangwi.soup.keyword.domain.Keyword;
import com.palangwi.soup.user.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Embeddable
public class UserKeywords {

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserKeyword> userKeywordList = new ArrayList<>();

    public void subscribe(User user, Keyword keyword) {
        userKeywordList.add(UserKeyword.create(user, keyword));
    }

    public void unSubscribe(Keyword keyword) {
        userKeywordList.stream()
                .filter(uk -> uk.getKeyword().equals(keyword))
                .findFirst()
                .ifPresent(UserKeyword::unsubscribe);
    }

    public boolean isAlreadySubscribed(Keyword keyword) {
        return userKeywordList.stream()
                .anyMatch(uk -> uk.getKeyword().equals(keyword) && uk.isSubscribed());
    }

    public Optional<UserKeyword> findByKeyword(Keyword keyword) {
        return userKeywordList.stream()
                .filter(uk -> uk.getKeyword().equals(keyword))
                .findFirst();
    }
}
