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
