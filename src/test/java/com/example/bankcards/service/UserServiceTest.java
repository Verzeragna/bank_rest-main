package com.example.bankcards.service;

import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.UserStatus;
import com.example.bankcards.exception.UserAlreadyExistException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CardService cardService;

    @Test
    void userRegistration_newUser() {
        var user = new User();
        user.setLogin("testuser");
        user.setPassword("password");

        when(userRepository.findByLogin("testuser")).thenReturn(Optional.empty());
        when(userRepository.hasAdmin()).thenReturn(true);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        userService.userRegistration(user);

        verify(userRepository).save(user);
        assertEquals(Role.USER, user.getRole());
    }

    @Test
    void userRegistration_newAdmin() {
        var user = new User();
        user.setLogin("testuser");
        user.setPassword("password");

        when(userRepository.findByLogin("testuser")).thenReturn(Optional.empty());
        when(userRepository.hasAdmin()).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        userService.userRegistration(user);

        verify(userRepository).save(user);
        assertEquals(Role.ADMIN, user.getRole());
    }

    @Test
    void userRegistration_userAlreadyExists() {
        var user = new User();
        user.setLogin("testuser");

        when(userRepository.findByLogin("testuser")).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistException.class, () -> {
            userService.userRegistration(user);
        });
    }

    @Test
    void deleteUser_success() {
        var user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(cardService).blockUserCards(user);
        verify(userRepository).save(user);
    }

    @Test
    void deleteUser_userNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUser(1L);
        });
    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(new User()));

        var result = userService.getAllUsers();

        assertEquals(1, result.size());
    }

    @Test
    void updateUser() {
        var user = new User();
        user.setId(1L);

        userService.updateUser(user);

        verify(userRepository).update(any(), any(), any(), any(), any(), eq(1L));
    }

    @Test
    void changeUserPassword_success() {
        var user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");

        userService.changeUserPassword(1L, "newPassword");

        verify(userRepository).updatePassword("encodedPassword", 1L);
    }

    @Test
    void changeUserPassword_userNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.changeUserPassword(1L, "newPassword");
        });
    }

    @Test
    void blockUserCards_success() {
        var user = new User();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.blockUserCards(1L);

        verify(cardService).blockUserCards(user);
    }

    @Test
    void blockUserCards_userNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.blockUserCards(1L);
        });
    }

    @Test
    void createUser_success() {
        var user = new User();
        user.setLogin("testuser");
        user.setPassword("password");

        when(userRepository.findByLogin("testuser")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");

        userService.createUser(user);

        verify(userRepository).save(user);
    }

    @Test
    void createUser_userAlreadyExists() {
        var user = new User();
        user.setLogin("testuser");

        when(userRepository.findByLogin("testuser")).thenReturn(Optional.of(new User()));

        assertThrows(UserAlreadyExistException.class, () -> {
            userService.createUser(user);
        });
    }

    @Test
    void changeUserStatus_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));

        userService.changeUserStatus(1L, UserStatus.BLOCKED);

        verify(userRepository).updateUserStatus(1L, UserStatus.BLOCKED);
    }

    @Test
    void changeUserStatus_userNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.changeUserStatus(1L, UserStatus.BLOCKED);
        });
    }
}
