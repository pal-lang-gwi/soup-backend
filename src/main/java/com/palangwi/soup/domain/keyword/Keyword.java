package com.palangwi.soup.domain.keyword;

import com.palangwi.soup.domain.BaseEntity;
import com.palangwi.soup.domain.userkeyword.UserKeyword;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "keyword")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Keyword extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String normalizedName;

    @OneToMany(mappedBy = "keyword", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserKeyword> userKeywords = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Source source;

    @Enumerated(EnumType.STRING)
    private Status status;

    public static Keyword of(String name, String normalizedName, Source source) {
        return Keyword.builder()
                .name(name)
                .normalizedName(normalizedName)
                .source(source)
                .status(Status.ACTIVE)
                .build();
    }

    @Builder
    private Keyword(String name, String normalizedName, Source source, Status status) {
        this.name = name;
        this.normalizedName = normalizedName;
        this.source = source;
        this.status = status;
    }

    // 추후 Keyword 도메인을 분리하게 된다면, 구독중인 사용자의 수를 어떻게 관리할 지 논의가 필요할 것 같습니다.
    public int getSubscribedCount() {
        return (int) userKeywords.stream()
                .filter(UserKeyword::isSubscribed)
                .count();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Keyword keyword = (Keyword) o;
        return id != null && id.equals(keyword.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}