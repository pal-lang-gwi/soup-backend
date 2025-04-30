package com.palangwi.soup.domain.userlog;

import com.palangwi.soup.domain.BaseEntity;
import com.palangwi.soup.domain.user.Gender;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "user_history")
public class UserHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Enumerated(EnumType.STRING)
    private ChangeType changeType;

    private Gender gender;

    private LocalDate birthDate;

    private String leaveReason;

    @Builder
    private UserHistory(String email, ChangeType changeType, Gender gender, LocalDate birthDate, String leaveReason) {
        this.email = email;
        this.changeType = changeType;
        this.gender = gender;
        this.birthDate = birthDate;
        this.leaveReason = leaveReason;
    }

    public static UserHistory ofDelete(String email, Gender gender, LocalDate birthDate, String leaveReason) {
        return UserHistory.builder()
                .email(email)
                .changeType(ChangeType.DELETE)
                .gender(gender)
                .birthDate(birthDate)
                .leaveReason(leaveReason)
                .build();
    }

    public static UserHistory ofCreate(String email, Gender gender, LocalDate birthDate) {
        return UserHistory.builder()
                .email(email)
                .changeType(ChangeType.CREATE)
                .gender(gender)
                .birthDate(birthDate)
                .build();
    }
}
