package com.palangwi.soup.mail.repository;

import com.palangwi.soup.mail.domain.MailError;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailErrorRepository extends JpaRepository<MailError, Long> {
}
