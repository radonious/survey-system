package edu.eltex.forms.repository;

import edu.eltex.forms.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * Определить, существует ли пользователь с определенным именем
     * @param username имя пользователя
     * @return true - существует, false - не существует
     */
    boolean existsByUsername(String username);


    /**
     * Найти пользователя по имени
     * @param username имя пользователя
     * @return entity user
     */
    Optional<User> findByUsername(String username);


    /**
     * Найти пользователя по его токену обновления
     * @param refreshToken токен обновления
     * @return entity user
     */
    Optional<User> findByRefreshToken(String refreshToken);
}
