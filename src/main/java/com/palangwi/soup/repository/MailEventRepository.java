package com.palangwi.soup.repository;

import com.palangwi.soup.domain.mail.MailEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailEventRepository extends JpaRepository<MailEvent, Long> {
}
