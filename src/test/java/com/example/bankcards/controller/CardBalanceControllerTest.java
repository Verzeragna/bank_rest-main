package com.example.bankcards.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bankcards.dto.MoneyTransferDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardBalanceException;
import com.example.bankcards.exception.GlobalExceptionHandler;
import com.example.bankcards.service.CardBalanceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@ExtendWith(MockitoExtension.class)
class CardBalanceControllerTest {

  private MockMvc mockMvc;

  @Mock private CardBalanceService cardBalanceService;

  @InjectMocks private CardBalanceController cardBalanceController;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    var validator = new LocalValidatorFactoryBean();
    validator.afterPropertiesSet();
    mockMvc =
        MockMvcBuilders.standaloneSetup(cardBalanceController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .setValidator(validator)
            .build();
  }

  private void setupUser() {
    var user = new User();
    user.setId(1L);
    user.setLogin("user");
    user.setRole(Role.USER);
    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken(
                user, null, Collections.singleton(new SimpleGrantedAuthority("USER"))));
  }

  @Test
  void transferMoney_success() throws Exception {
    setupUser();
    MoneyTransferDto dto = new MoneyTransferDto("1234", "5678", BigDecimal.TEN);

    mockMvc
        .perform(
            post("/balance/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk());
  }

  @Test
  void transferMoney_invalidRequest() throws Exception {
    setupUser();
    MoneyTransferDto dto = new MoneyTransferDto(null, "5678", BigDecimal.TEN);

    mockMvc
        .perform(
            post("/balance/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void transferMoney_serviceThrowsException() throws Exception {
    setupUser();
    MoneyTransferDto dto = new MoneyTransferDto("1234", "5678", BigDecimal.TEN);

    doThrow(new CardBalanceException())
        .when(cardBalanceService)
        .transferMoney(any(User.class), eq("1234"), eq("5678"), eq(BigDecimal.TEN));

    mockMvc
        .perform(
            post("/balance/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isBadRequest());
  }
}
