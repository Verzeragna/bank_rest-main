package com.example.bankcards.service;

import com.example.bankcards.dto.auth.AuthenticationRequest;
import com.example.bankcards.dto.auth.JwtAuthenticationDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.IncorrectPasswordException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.jwt.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @InjectMocks
    private AuthenticationService authenticationService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Test
    void authenticate_success() {
        var request = new AuthenticationRequest("testuser", "password");
        var user = new User();
        user.setPassword("encodedPassword");

        when(userRepository.findByLogin("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(true);
        when(jwtService.generateAuthToken("testuser")).thenReturn(new JwtAuthenticationDto("test-token"));

        var result = authenticationService.authenticate(request);

        assertNotNull(result);
        assertEquals("test-token", result.token());
    }

    @Test
    void authenticate_userNotFound() {
        var request = new AuthenticationRequest("testuser", "password");

        when(userRepository.findByLogin("testuser")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            authenticationService.authenticate(request);
        });
    }

    @Test
    void authenticate_incorrectPassword() {
        var request = new AuthenticationRequest("testuser", "password");
        var user = new User();
        user.setPassword("encodedPassword");

        when(userRepository.findByLogin("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encodedPassword")).thenReturn(false);

        assertThrows(IncorrectPasswordException.class, () -> {
            authenticationService.authenticate(request);
        });
    }
}
