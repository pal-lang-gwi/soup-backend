package com.palangwi.soup.repository;

import com.palangwi.soup.domain.mail.MailError;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MailErrorRepository extends JpaRepository<MailError, Long> {
}
