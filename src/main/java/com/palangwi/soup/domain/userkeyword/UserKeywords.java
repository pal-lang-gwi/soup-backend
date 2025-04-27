package com.palangwi.soup.domain.userkeyword;

import com.palangwi.soup.domain.keyword.Keyword;
import com.palangwi.soup.domain.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

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
}
