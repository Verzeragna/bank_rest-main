package com.example.bankcards.service;

import com.example.bankcards.dto.UserPasswordDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.UserStatus;
import com.example.bankcards.exception.UserAlreadyExistException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final CardService cardService;

  @Transactional
  public void userRegistration(User user) {
    var userDb = userRepository.findByLogin(user.getLogin());
    if (userDb.isPresent()) {
      throw new UserAlreadyExistException();
    }
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    user.setCreatedAt(LocalDateTime.now());
    user.setStatus(UserStatus.ACTIVE);
    if (userRepository.hasAdmin()) {
      user.setRole(Role.USER);
    } else {
      user.setRole(Role.ADMIN);
    }
    userRepository.save(user);
  }

  @Transactional
  public void deleteUser(Long id) {
    userRepository
        .findById(id)
        .ifPresentOrElse(
            user -> {
              user.setStatus(UserStatus.DELETED);
              cardService.blockUserCards(user);
              userRepository.save(user);
            },
            () -> {
              throw new UserNotFoundException();
            });
  }

  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  @Transactional
  public void updateUser(User user) {
    userRepository.update(user.getName(), user.getLastName(), user.getSurname(), user.getLogin(), user.getRole(), user.getId());
  }

  @Transactional
  public void changeUserPassword(Long userId, String password) {
    userRepository
        .findById(userId)
        .ifPresentOrElse(
            user -> userRepository.updatePassword(passwordEncoder.encode(password), userId),
            () -> {
              throw new UserNotFoundException();
            });
  }

  public void blockUserCards(Long userId) {
    userRepository
        .findById(userId)
        .ifPresentOrElse(
            cardService::blockUserCards,
            () -> {
              throw new UserNotFoundException();
            });
  }

  @Transactional
  public void createUser(User user) {
    var userDb = userRepository.findByLogin(user.getLogin());
    if (userDb.isPresent()) {
      throw new UserAlreadyExistException();
    }
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    user.setCreatedAt(LocalDateTime.now());
    user.setStatus(UserStatus.ACTIVE);
    userRepository.save(user);
  }
}
