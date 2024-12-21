package edu.eltex.forms.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
public class JwtServiceTest {

    private static final SecretKey SECRET_KEY = JwtService.SECRET_KEY;
    private JwtService jwtService;

    @BeforeEach
    public void setUp() {
        jwtService = new JwtService();
    }

    @Test
    public void testExtractUsername() {
        String username = "testUser";
        String token = jwtService.generateToken(new User(username, "password", new ArrayList<>()));

        String extractedUsername = jwtService.extractUsername(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    public void testExtractClaim() {
        String username = "testUser";
        String token = jwtService.generateToken(new User(username, "password", new ArrayList<>()));

        String extractedUsername = jwtService.extractClaim(token, Claims::getSubject);
        assertEquals(username, extractedUsername);
    }

    @Test
    public void testValidateValidToken() {
        String username = "testUser";
        UserDetails userDetails = new User(username, "password", new ArrayList<>());
        String token = jwtService.generateToken(userDetails);

        boolean isValid = jwtService.validateToken(token, userDetails);
        assertTrue(isValid, "Token should be valid");
    }

    @Test
    public void testValidateInvalidToken() {
        String validUsername = "validUser";
        String invalidUsername = "invalidUser";

        UserDetails userDetails = new User(validUsername, "password", new ArrayList<>());
        UserDetails invalidUserDetails = new User(invalidUsername, "password", new ArrayList<>());
        String token = jwtService.generateToken(userDetails);

        boolean isValid = jwtService.validateToken(token, invalidUserDetails);
        assertFalse(isValid);
    }

    @Test
    public void testIsTokenNotExpired() {
        String username = "testUser";
        UserDetails userDetails = new User(username, "password", new ArrayList<>());
        String token = jwtService.generateToken(userDetails);

        boolean isExpired = jwtService.isTokenExpired(token);
        assertFalse(isExpired);
    }

    @Test
    public void testIsTokenExpired() throws InterruptedException {
        String username = "testUser";
        UserDetails userDetails = new User(username, "password", new ArrayList<>());

        String token = jwtService.generateToken(userDetails);

        Claims claims = jwtService.extractAllClaims(token);

        claims.setExpiration(new Date(System.currentTimeMillis() + 1000));
        String expiredToken = Jwts.builder()
                .setClaims(claims)
                .signWith(SECRET_KEY)
                .compact();
        Thread.sleep(1500);

        boolean isExpired = jwtService.isTokenExpired(expiredToken);
        assertTrue(isExpired);
    }


    @Test
    public void testGenerateToken() {
        String username = "testUser";
        UserDetails userDetails = new User(username, "password", new ArrayList<>());

        String token = jwtService.generateToken(userDetails);
        assertNotNull(token);
        assertTrue(token.startsWith("eyJhbGciOiJIUzI1NiJ9"));
    }
}
