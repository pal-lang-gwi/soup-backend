package com.palangwi.soup.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.palangwi.soup.domain.keyword.Keyword;
import com.palangwi.soup.domain.keyword.Source;

@SpringBootTest
@Transactional
class KeywordRepositoryTest {

    @Autowired
    private KeywordRepository keywordRepository;

    @BeforeEach
    void setUp() {
        Keyword keyword1 = Keyword.of("키워드1", "키워드1", Source.USER_REQUEST);
        Keyword keyword2 = Keyword.of("키워드2", "키워드2", Source.MANUAL);
        keywordRepository.saveAll(Arrays.asList(keyword1, keyword2));
    }

    @DisplayName("키워드 이름으로 존재 여부를 확인한다.")
    @Test
    void existsByName() {
        boolean exists = keywordRepository.existsByName("키워드1");
        assertThat(exists).isTrue();
    }

    @DisplayName("존재하지 않는 키워드 이름으로 존재 여부를 확인하면 false를 반환한다.")
    @Test
    void existsByName_false() {
        boolean exists = keywordRepository.existsByName("없는키워드");
        assertThat(exists).isFalse();
    }

    @DisplayName("키워드 이름으로 키워드를 조회한다.")
    @Test
    void findByName() {
        Optional<Keyword> result = keywordRepository.findByName("키워드1");
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("키워드1");
    }

    @DisplayName("존재하지 않는 키워드 이름으로 조회하면 빈 값을 반환한다.")
    @Test
    void findByName_empty() {
        Optional<Keyword> result = keywordRepository.findByName("없는키워드");
        assertThat(result).isEmpty();
    }

    @DisplayName("여러 키워드 이름으로 키워드 리스트를 조회한다.")
    @Test
    void findAllByNameIn() {
        List<Keyword> result = keywordRepository.findAllByNameIn(Arrays.asList("키워드1", "키워드2", "없는키워드"));
        assertThat(result).hasSize(2);
        assertThat(result).extracting("name").containsExactlyInAnyOrder("키워드1", "키워드2");
    }
}