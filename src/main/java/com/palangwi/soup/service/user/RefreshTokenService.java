package com.palangwi.soup.service.user;

import com.palangwi.soup.exception.user.InvalidRefreshTokenException;
import com.palangwi.soup.repository.user.RefreshTokenRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    @Value("${security.jwt.refresh-token.ttl}")
    private long refreshTokenTtl;

    public void saveRefreshToken(String refreshToken, Long userId) {
        refreshTokenRedisRepository.save(refreshToken, userId, refreshTokenTtl);
    }

    public Long validateRefreshToken(String refreshToken) {
        String userIdStr = refreshTokenRedisRepository.findUserIdByRefreshToken(refreshToken);
        if (userIdStr == null) {
            throw new InvalidRefreshTokenException();
        }
        return Long.valueOf(userIdStr);
    }

    public void invalidateRefreshToken(String refreshToken) {
        refreshTokenRedisRepository.delete(refreshToken);
    }
}
