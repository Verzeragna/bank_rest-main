package com.example.bankcards.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bankcards.dto.UserCreationDto;
import com.example.bankcards.dto.UserDto;
import com.example.bankcards.dto.UserPasswordDto;
import com.example.bankcards.dto.UserRegistrationDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.UserStatus;
import com.example.bankcards.exception.GlobalExceptionHandler;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDateTime;
import java.util.Collections;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  private MockMvc mockMvc;

  @Mock private UserService userService;

  @Mock private UserMapper userMapper;

  @Mock private UserRepository userRepository;

  @InjectMocks private UserController userController;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    objectMapper.registerModule(new JavaTimeModule());
    var validator = new LocalValidatorFactoryBean();
    validator.afterPropertiesSet();
    mockMvc =
        MockMvcBuilders.standaloneSetup(userController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
            .setValidator(validator)
            .build();
  }

  private void setupAdmin() {
    var admin = new User();
    admin.setId(1L);
    admin.setLogin("admin");
    admin.setRole(Role.ADMIN);
    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken(
                admin, null, Collections.singleton(new SimpleGrantedAuthority("ADMIN"))));
  }

  @Test
  void userRegistration_success() throws Exception {
    var dto = new UserRegistrationDto("test", "test", "test", "test@test.com", "password");
    var user = new User();
    user.setPassword("password");
    when(userMapper.toUser(dto)).thenReturn(user);

    mockMvc
        .perform(
            post("/users/registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk());
  }

  @Test
  void createUser_success() throws Exception {
    setupAdmin();
    var dto = new UserCreationDto("test", "test", "test", Role.USER, "test@test.com", "password");
    var user =
        new User(
            null, "test", "test", "test", "test@test.com", "password", null, Role.USER, null, null);
    user.setPassword("password");
    when(userMapper.toUser(dto)).thenReturn(user);

    mockMvc
        .perform(
            post("/users/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk());
  }

  @Test
  void deleteUser_success() throws Exception {
    setupAdmin();
    mockMvc.perform(delete("/users/1")).andExpect(status().isOk());
  }

  @Test
  void getAllUsers_success() throws Exception {
    setupAdmin();
    when(userService.getAllUsers()).thenReturn(Collections.singletonList(new User()));

    mockMvc
        .perform(get("/users/list"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1));
  }

  @Test
  void updateUser_success() throws Exception {
    setupAdmin();
    var dto =
        new UserDto(
            1L,
            "test",
            "test",
            "test",
            "test@test.com",
            UserStatus.ACTIVE,
            Role.USER,
            LocalDateTime.now());
    var user =
        new User(
            1L,
            "test",
            "test",
            "test",
            "test@test.com",
            "",
            UserStatus.ACTIVE,
            Role.USER,
            LocalDateTime.now(),
            null);
    when(userMapper.toEntity(dto)).thenReturn(user);

    mockMvc
        .perform(
            put("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk());
  }

  @Test
  void changeUserPassword_success() throws Exception {
    setupAdmin();
    UserPasswordDto dto = new UserPasswordDto(1L, "newPassword");

    mockMvc
        .perform(
            patch("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk());
  }

  @Test
  void blockUserCards_success() throws Exception {
    setupAdmin();
    mockMvc.perform(post("/users/cards/block").param("userId", "1")).andExpect(status().isOk());
  }
}
