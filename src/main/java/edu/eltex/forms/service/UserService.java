package edu.eltex.forms.service;

import edu.eltex.forms.dto.UserRequestDto;
import edu.eltex.forms.dto.UserResponseDto;
import edu.eltex.forms.entities.User;
import edu.eltex.forms.mapper.UserMapper;
import edu.eltex.forms.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static edu.eltex.forms.enums.UserRole.USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Запрос всех существующих пользователей
     * @return список {@link UserResponseDto}
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<UserResponseDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Поиск пользователя по ID
     * @param id ID пользователя
     * @return {@link UserResponseDto} ответ
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public UserResponseDto findUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return userMapper.toDto(user);
    }

    /**
     * Поиск пользователя по имени
     * @param username имя пользователя
     * @return {@link UserResponseDto} ответ
     */
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public UserResponseDto findUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
        return userMapper.toDto(user);
    }

    /** Создание нового пользователя
     * @param userRequestDto запрос
     * @return {@link UserResponseDto} ответ
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        userRequestDto.setRole(USER);
        User user = userMapper.toEntity(userRequestDto);
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new EntityExistsException("User with username: " + user.getUsername() + " already exists");
        }
        user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    /**
     * Обновление данных о пользователе
     * @param id ID пользователя
     * @param userRequestDto запрос с новыми данными
     * @return {@link UserResponseDto} ответ
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public UserResponseDto updateUser(Integer id, UserRequestDto userRequestDto) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        User updatedUser = userMapper.toEntity(userRequestDto);
        updatedUser.setId(id);
        User savedUser = userRepository.save(updatedUser);
        return userMapper.toDto(savedUser);
    }

    /**
     * Удаления пользователя по ID
     * @param id ID пользователя
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void deleteUser(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Сохранение токена обновления для пользователя
     * @param username имя пользователя
     * @param refreshToken токен обновления
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void saveRefreshToken(String username, String refreshToken) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
        user.setRefreshToken(refreshToken);
        userRepository.save(user);
    }

    @PostConstruct
    public void init() {
        log.info("Creator username: 'creator'; creator password: 'password'");
    }
}