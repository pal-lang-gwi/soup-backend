package com.palangwi.soup.domain.keyword;

import com.palangwi.soup.domain.BaseEntity;
import com.palangwi.soup.domain.userkeyword.UserKeyword;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

    private Source source;

    private boolean visible;

    private boolean deleted;

    public static Keyword generateKeyword(String name, String normalizedName, Source source) {
        return Keyword.builder()
                .name(name)
                .normalizedName(normalizedName)
                .source(source)
                .visible(true)
                .deleted(false)
                .build();
    }

    @Builder
    private Keyword(String name, String normalizedName, Source source, boolean visible, boolean deleted) {
        this.name = name;
        this.normalizedName = normalizedName;
        this.source = source;
        this.visible = visible;
        this.deleted = deleted;
    }
}