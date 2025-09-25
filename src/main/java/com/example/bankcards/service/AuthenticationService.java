package com.example.bankcards.service;

import com.example.bankcards.dto.auth.AuthenticationRequest;
import com.example.bankcards.dto.auth.JwtAuthenticationDto;
import com.example.bankcards.exception.IncorrectPasswordException;
import com.example.bankcards.exception.UserNotFoundException;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  /**
   * Authenticates a user and returns a JWT token.
   *
   * @param authRequest The authentication request containing login and password.
   * @return A DTO containing the JWT access and refresh tokens.
   * @throws UserNotFoundException if the user with the given login is not found.
   * @throws IncorrectPasswordException if the provided password is incorrect.
   */
  public JwtAuthenticationDto authenticate(AuthenticationRequest authRequest) {
    var userOpt = userRepository.findByLogin(authRequest.getLogin());
    if (userOpt.isEmpty()) {
      throw new UserNotFoundException();
    }
    var user = userOpt.get();
    if (passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
      return jwtService.generateAuthToken(authRequest.getLogin());
    }
    throw new IncorrectPasswordException();
  }
}
