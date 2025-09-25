package com.example.bankcards.service;

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

  /**
   * Registers a new user.
   *
   * @param user The user to register.
   * @throws UserAlreadyExistException if a user with the same login already exists.
   */
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

  /**
   * Deletes a user by their ID.
   *
   * @param id The ID of the user to delete.
   * @throws UserNotFoundException if the user with the given ID is not found.
   */
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

  /**
   * Retrieves a list of all users.
   *
   * @return A list of all users.
   */
  public List<User> getAllUsers() {
    return userRepository.findAll();
  }

  /**
   * Updates a user's information.
   *
   * @param user The user with updated information.
   */
  @Transactional
  public void updateUser(User user) {
    userRepository.update(
        user.getName(),
        user.getLastName(),
        user.getSurname(),
        user.getLogin(),
        user.getRole(),
        user.getId());
  }

  /**
   * Changes a user's password.
   *
   * @param userId The ID of the user.
   * @param password The new password.
   * @throws UserNotFoundException if the user with the given ID is not found.
   */
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

  /**
   * Blocks all cards for a specific user.
   *
   * @param userId The ID of the user.
   * @throws UserNotFoundException if the user with the given ID is not found.
   */
  public void blockUserCards(Long userId) {
    userRepository
        .findById(userId)
        .ifPresentOrElse(
            cardService::blockUserCards,
            () -> {
              throw new UserNotFoundException();
            });
  }

  /**
   * Creates a new user.
   *
   * @param user The user to create.
   * @throws UserAlreadyExistException if a user with the same login already exists.
   */
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

  /**
   * Changes the status of a user.
   *
   * @param userId The ID of the user.
   * @param userStatus The new status of the user.
   * @throws UserNotFoundException if the user with the given ID is not found.
   */
  @Transactional
  public void changeUserStatus(Long userId, UserStatus userStatus) {
    userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
    userRepository.updateUserStatus(userId, userStatus);
  }
}
