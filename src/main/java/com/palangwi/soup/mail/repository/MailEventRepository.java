package com.palangwi.soup.mail.repository;

import com.palangwi.soup.mail.domain.MailEvent;
import com.palangwi.soup.mail.domain.MailType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MailEventRepository extends JpaRepository<MailEvent, Long> {
    Optional<MailEvent> findTopByTypeOrderByCreatedDateDesc(MailType type);
}
