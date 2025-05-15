package com.palangwi.soup.service.user;

import com.palangwi.soup.domain.user.User;
import com.palangwi.soup.domain.userlog.UserHistory;
import com.palangwi.soup.dto.user.UserDeleteRequestDto;
import com.palangwi.soup.repository.user.UserHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserHistoryService {

    private final UserHistoryRepository userHistoryRepository;

    @Transactional
    public void saveCreateHistory(User user) {
        UserHistory userHistory = UserHistory.ofCreate(
                user.getEmail(), user.getGender(), user.getBirthDate()
        );
        userHistoryRepository.save(userHistory);
    }

    @Transactional
    public void saveDeleteHistory(User user, UserDeleteRequestDto request) {
        UserHistory userHistory = UserHistory.ofDelete(
                user.getEmail(), user.getGender(), user.getBirthDate(), request.reason()
        );
        userHistoryRepository.save(userHistory);
    }
}
