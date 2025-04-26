package com.palangwi.soup.repository;

import com.palangwi.soup.domain.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    boolean existsByNickname(String nickname);

    Optional<User> findByUsernameAndIsDeletedFalse(String username);

    Optional<User> findAllByIsDeletedFalse();
}
