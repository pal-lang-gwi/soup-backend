package com.palangwi.soup.domain.mail;

import com.palangwi.soup.domain.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MailEvent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private MailType type;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    private boolean sendSuccess;

    private boolean opened;

    private LocalDateTime openedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mail_error_id")
    private MailError mailError;

    public MailEvent(Long userId, MailType type, LocalDateTime sentAt, boolean sendSuccess) {
        this.userId = userId;
        this.type = type;
        this.sentAt = sentAt;
        this.sendSuccess = sendSuccess;
    }

    public static MailEvent success(Long userId, MailType type) {
        LocalDateTime now = LocalDateTime.now();
        return new MailEvent(userId, type, now, true);
    }

    public static MailEvent fail(Long userId, MailType type) {
        LocalDateTime now = LocalDateTime.now();
        return new MailEvent(userId, type, now, false);
    }

    public void markAsOpen() {
        this.opened = true;
        this.openedAt = LocalDateTime.now();
    }

    public void markSuccess() {
        this.sendSuccess = true;
    }

    public void markFailureWithReason(MailError error) {
        this.sendSuccess = false;
        this.mailError = error;
    }
}
