package com.palangwi.soup.repository;

import com.palangwi.soup.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    Optional<User> findByUsernameAndIsDeletedFalse(String username);
    boolean existsByNickname(String nickname);
}
