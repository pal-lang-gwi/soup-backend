package com.palangwi.soup.mail.domain;

import com.palangwi.soup.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MailError extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String reason;

    public static MailError of(String code, String reason) {
        MailError error = new MailError();
        error.code = code;
        error.reason = reason;
        return error;
    }
}
