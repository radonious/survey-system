package edu.eltex.forms.controller;

import edu.eltex.forms.dto.AuthRequestDto;
import edu.eltex.forms.dto.AuthResponseDto;
import edu.eltex.forms.dto.LogoutRequestDto;
import edu.eltex.forms.dto.UserRequestDto;
import edu.eltex.forms.entities.User;
import edu.eltex.forms.enums.UserRole;
import edu.eltex.forms.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class AuthControllerTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15.3")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpassword");
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;


    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void registerUser() {
        UserRequestDto registrationRequest = UserRequestDto.builder()
                .username("testUser")
                .password("password")
                .role(UserRole.USER)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserRequestDto> request = new HttpEntity<>(registrationRequest, headers);

        ResponseEntity<Void> response = restTemplate.postForEntity("/api/v1/auth/register", request, Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        User user = userRepository.findByUsername("testUser").get();
        assertNotNull(user);
        assertEquals("testUser", user.getUsername());
    }

    @Test
    void login() {
        registerUser();

        AuthRequestDto authRequest = AuthRequestDto.builder()
                .username("testUser")
                .password("password")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AuthRequestDto> request = new HttpEntity<>(authRequest, headers);

        ResponseEntity<AuthResponseDto> response = restTemplate.postForEntity("/api/v1/auth/login", request, AuthResponseDto.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getAccessToken());
        assertNotNull(response.getBody().getRefreshToken());

        String refreshToken = response.getBody().getRefreshToken();
        assertNotNull(userRepository.findByRefreshToken(refreshToken));
    }

    @Test
    void logoutUser() {
        login();

        LogoutRequestDto logoutRequest = new LogoutRequestDto("testUser");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LogoutRequestDto> request = new HttpEntity<>(logoutRequest, headers);

        ResponseEntity<Void> response = restTemplate.postForEntity("/api/v1/auth/logout", request, Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertNull(userRepository.findByUsername("testUser").get().getRefreshToken());
    }
}