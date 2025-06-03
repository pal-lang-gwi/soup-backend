package com.palangwi.soup.repository.mail;

import com.palangwi.soup.domain.mail.MailEvent;
import com.palangwi.soup.domain.mail.MailType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailEventRepository extends JpaRepository<MailEvent, Long> {
    Optional<MailEvent> findTopByTypeOrderByCreatedDateDesc(MailType type);
}
