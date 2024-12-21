package edu.eltex.forms.service;

import edu.eltex.forms.dto.RefreshTokenResponseDto;
import edu.eltex.forms.entities.User;
import edu.eltex.forms.exception.TokenRefreshException;
import edu.eltex.forms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    /**
     * Создает токен обновления
     * @param username имя пользователя
     * @return новый токен
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String createRefreshToken(String username) {
        String refreshToken = UUID.randomUUID().toString();
        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(
                                () -> new UsernameNotFoundException("User not found with username: " + username));
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
        return refreshToken;
    }

    /**
     * Обновляет токен доступа
     * @param refreshToken токен обновления
     * @return {@link RefreshTokenResponseDto} ответ
     */
    public RefreshTokenResponseDto refreshAccessToken(String refreshToken) {
        User user =
                userRepository
                        .findByRefreshToken(refreshToken)
                        .orElseThrow(
                                () -> new TokenRefreshException(refreshToken, "Refresh token not exists!"));
        if (user.getRefreshToken().equals(refreshToken)) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            return new RefreshTokenResponseDto(jwtService.generateToken(userDetails));
        } else {
            throw new TokenRefreshException(refreshToken, "Refresh token is not valid!");
        }
    }

    /**
     * Удаляет токен обновления у пользователя
     * @param username имя пользователя
     */
    public void deleteRefreshToken(String username) {
        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(
                                () -> new UsernameNotFoundException("User not found with username: " + username));
        user.setRefreshToken(null);
        userRepository.save(user);
    }
}
