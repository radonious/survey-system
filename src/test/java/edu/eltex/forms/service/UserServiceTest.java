package edu.eltex.forms.service;

import edu.eltex.forms.dto.UserRequestDto;
import edu.eltex.forms.dto.UserResponseDto;
import edu.eltex.forms.enums.UserRole;
import edu.eltex.forms.mapper.UserMapper;
import edu.eltex.forms.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@Transactional
public class UserServiceTest {

    @Container
    public static GenericContainer<?> h2Container = new GenericContainer<>("buildo/h2database")
            .withExposedPorts(9092);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setup() {
        userService = new UserService(passwordEncoder, userRepository, userMapper);
        userRepository.deleteAll();
    }

    @Test
    public void testFindAllUsers() {
        userService.createUser(UserRequestDto.builder()
                .username("user1")
                .password("password")
                .role(UserRole.USER)
                .build());

        userService.createUser(UserRequestDto.builder()
                .username("user2")
                .password("password")
                .role(UserRole.CREATOR)
                .build());

        List<UserResponseDto> users = userService.findAllUsers();

        assertThat(users).hasSize(2);
    }

    @Test
    public void testCreateUserSuccess() {
        UserRequestDto userRequest = UserRequestDto.builder()
                .username("testuser")
                .password("password")
                .role(UserRole.USER)
                .build();

        UserResponseDto createdUser = userService.createUser(userRequest);

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getUsername()).isEqualTo("testuser");
    }

    @Test
    public void testCreateUserDuplicateUsername() {
        UserRequestDto userRequest = UserRequestDto.builder()
                .username("duplicateUser")
                .password("password")
                .role(UserRole.USER)
                .build();

        userService.createUser(userRequest);

        assertThrows(EntityExistsException.class, () -> userService.createUser(userRequest));
    }
}
