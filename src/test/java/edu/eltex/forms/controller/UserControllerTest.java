package edu.eltex.forms.controller;

import edu.eltex.forms.dto.AuthRequestDto;
import edu.eltex.forms.dto.AuthResponseDto;
import edu.eltex.forms.dto.UserRequestDto;
import edu.eltex.forms.dto.UserResponseDto;
import edu.eltex.forms.entities.User;
import edu.eltex.forms.enums.UserRole;
import edu.eltex.forms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class UserControllerTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15.3")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpassword");
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    private String userAccessToken;
    private String creatorAccessToken;
    private Integer userId;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        userId = registerUser("user", "password", UserRole.USER);
        userAccessToken = loginUser("user", "password").getAccessToken();

        registerUser("creator", "password", UserRole.CREATOR);
        creatorAccessToken = loginUser("creator", "password").getAccessToken();
    }

    private Integer registerUser(String username, String password, UserRole role) {
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();
        return userRepository.save(user).getId();
    }

    private AuthResponseDto loginUser(String username, String password) {
        AuthRequestDto authRequest = AuthRequestDto.builder()
                .username(username)
                .password(password)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AuthRequestDto> request = new HttpEntity<>(authRequest, headers);
        ResponseEntity<AuthResponseDto> response = restTemplate.postForEntity("/api/v1/auth/login", request, AuthResponseDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());

        return response.getBody();
    }

    @Test
    void getAllUsers() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(creatorAccessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<List> response = restTemplate.exchange("/api/v1/users", HttpMethod.GET, request, List.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void getAllUsersWithUserRole() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userAccessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange("/api/v1/users", HttpMethod.GET, request, Void.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void getUserByIdWithOwner() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userAccessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<UserResponseDto> response = restTemplate.exchange("/api/v1/users/" + userId, HttpMethod.GET, request, UserResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("user", response.getBody().getUsername());
    }

    @Test
    void getUserByIdWithNotOwner() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userAccessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<UserResponseDto> response = restTemplate.exchange("/api/v1/users/" + 2, HttpMethod.GET, request, UserResponseDto.class);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void createUser() {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .username("newUser")
                .password("password")
                .role(UserRole.USER)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(creatorAccessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserRequestDto> request = new HttpEntity<>(userRequestDto, headers);

        ResponseEntity<UserResponseDto> response = restTemplate.postForEntity("/api/v1/users", request, UserResponseDto.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("newUser", response.getBody().getUsername());
    }

    @Test
    void updateUser() {
        UserRequestDto userRequestDto = UserRequestDto.builder()
                .username("updatedUser")
                .password("newpassword")
                .role(UserRole.USER)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(creatorAccessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserRequestDto> request = new HttpEntity<>(userRequestDto, headers);

        ResponseEntity<UserResponseDto> response = restTemplate.exchange("/api/v1/users/" + userId, HttpMethod.PUT, request, UserResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("updatedUser", response.getBody().getUsername());
    }

    @Test
    void deleteUser() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(creatorAccessToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange("/api/v1/users/" + userId, HttpMethod.DELETE, request, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        assertFalse(userRepository.existsByUsername("user"));
    }
}