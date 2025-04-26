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
    private String type;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    private boolean sendSuccess;

    private boolean isRead;

    private LocalDateTime readAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mail_error_id")
    private MailError mailError;

    public MailEvent(Long userId, String type, LocalDateTime sentAt, boolean sendSuccess) {
        this.userId = userId;
        this.type = type;
        this.sentAt = sentAt;
        this.sendSuccess = sendSuccess;
    }

    public static MailEvent success(Long userId, String type) {
        LocalDateTime now = LocalDateTime.now();
        return new MailEvent(userId, type, now, true);
    }

    public static MailEvent fail(Long userId, String type) {
        LocalDateTime now = LocalDateTime.now();
        return new MailEvent(userId, type, now, false);
    }

    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }

    public void markSuccess() {
        this.sendSuccess = true;
    }
}
