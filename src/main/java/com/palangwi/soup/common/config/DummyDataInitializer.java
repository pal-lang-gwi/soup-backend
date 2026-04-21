package com.palangwi.soup.common.config;

import com.palangwi.soup.common.security.Role;
import com.palangwi.soup.keyword.domain.Keyword;
import com.palangwi.soup.keyword.domain.Source;
import com.palangwi.soup.keyword.domain.Status;
import com.palangwi.soup.keyword.repository.KeywordRepository;
import com.palangwi.soup.subscription.domain.UserKeyword;
import com.palangwi.soup.subscription.repository.UserKeywordRepository;
import com.palangwi.soup.user.domain.Gender;
import com.palangwi.soup.user.domain.User;
import com.palangwi.soup.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Slf4j
@RequiredArgsConstructor
@Profile("local")
@Component
public class DummyDataInitializer implements CommandLineRunner {

        private final UserRepository userRepository;
        private final KeywordRepository keywordRepository;
        private final UserKeywordRepository userKeywordRepository;

        @Override
        @Transactional
        public void run(String... args) throws Exception {
                if (userRepository.count() == 0 && keywordRepository.count() == 0) {
                        log.info("테이블에 데이터가 존재하지 않아 임의의 데이터를 삽입합니다...");

                        // 1. User 생성
                        User user1 = User.builder()
                                        .email("kt65346337@gmail.com")
                                        .username("dummyUser1")
                                        .nickname("더미유저1")
                                        .role(Role.USER)
                                        .gender(Gender.MALE)
                                        .birthDate(LocalDate.of(1990, 1, 1))
                                        .providerId("google_dummy_1")
                                        .profileImageUrl("https://sample.png")
                                        .deleted(false)
                                        .build();
                        userRepository.save(user1);

                        User user2 = User.builder()
                                        .email("ssafygwangju6337@gmail.com")
                                        .username("dummyUser2")
                                        .nickname("더미유저2")
                                        .role(Role.USER)
                                        .gender(Gender.FEMALE)
                                        .birthDate(LocalDate.of(1995, 5, 5))
                                        .providerId("kakao_dummy_2")
                                        .profileImageUrl("https://sample.png")
                                        .deleted(false)
                                        .build();
                        userRepository.save(user2);

                        // 2. Keyword 생성
                        Keyword keyword1 = Keyword.builder()
                                        .name("빅데이터")
                                        .normalizedName("빅데이터")
                                        .source(Source.MANUAL)
                                        .status(Status.ACTIVE)
                                        .firstRequestUser(user1)
                                        .build();
                        keywordRepository.save(keyword1);

                        Keyword keyword2 = Keyword.builder()
                                        .name("사이버 보안")
                                        .normalizedName("사이버 보안")
                                        .source(Source.MANUAL)
                                        .status(Status.ACTIVE)
                                        .firstRequestUser(user1)
                                        .build();
                        keywordRepository.save(keyword2);

                        Keyword keyword3 = Keyword.builder()
                                        .name("인공지능")
                                        .normalizedName("인공지능")
                                        .source(Source.MANUAL)
                                        .status(Status.ACTIVE)
                                        .firstRequestUser(user2)
                                        .build();
                        keywordRepository.save(keyword3);

                        if (userKeywordRepository.count() == 0) {
                                UserKeyword userKeyword1 = UserKeyword.builder()
                                                .user(user1)
                                                .keyword(keyword1)
                                                .subscribed(true)
                                                .build();
                                userKeywordRepository.save(userKeyword1);

                                UserKeyword userKeyword2 = UserKeyword.builder()
                                                .user(user1)
                                                .keyword(keyword2)
                                                .subscribed(true)
                                                .build();
                                userKeywordRepository.save(userKeyword2);

                                UserKeyword userKeyword3 = UserKeyword.builder()
                                                .user(user2)
                                                .keyword(keyword3)
                                                .subscribed(true)
                                                .build();
                                userKeywordRepository.save(userKeyword3);
                        }

                        log.info("임의의 데이터 삽입이 완료되었습니다.");
                } else {
                        log.info("데이터가 이미 존재하므로, 초기 데이터를 삽입하지 않습니다.");
                }
        }
}
