package com.example.bankcards.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bankcards.dto.AdminCardDto;
import com.example.bankcards.dto.CardBlockRequestDto;
import com.example.bankcards.dto.PaginationDto;
import com.example.bankcards.dto.UserCardDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.GlobalExceptionHandler;
import com.example.bankcards.service.CardService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@ExtendWith(MockitoExtension.class)
class CardControllerTest {

  private MockMvc mockMvc;

  @Mock private CardService cardService;

  @InjectMocks private CardController cardController;

  @BeforeEach
  void setUp() {
    var validator = new LocalValidatorFactoryBean();
    validator.afterPropertiesSet();
    mockMvc =
        MockMvcBuilders.standaloneSetup(cardController)
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
            new UsernamePasswordAuthenticationToken(admin, null, admin.getAuthorities()));
  }

  private User setupUser() {
    var user = new User();
    user.setId(2L);
    user.setLogin("user");
    user.setRole(Role.USER);
    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
    return user;
  }

  // Admin tests

  @Test
  void createCardForUser_success() throws Exception {
    setupAdmin();
    mockMvc.perform(post("/cards/create").param("userId", "1")).andExpect(status().isOk());
  }

  @Test
  void createCardForUser_nullUserId() throws Exception {
    setupAdmin();
    mockMvc.perform(post("/cards/create")).andExpect(status().isBadRequest());
  }

  @Test
  void blockCard_success() throws Exception {
    setupAdmin();
    mockMvc.perform(post("/cards/1/block")).andExpect(status().isOk());
  }

  @Test
  void activateCard_success() throws Exception {
    setupAdmin();
    mockMvc.perform(post("/cards/1/activate")).andExpect(status().isOk());
  }

  @Test
  void deleteCard_success() throws Exception {
    setupAdmin();
    mockMvc.perform(delete("/cards/1")).andExpect(status().isOk());
  }

  @Test
  void getAllCards_success() throws Exception {
    setupAdmin();
    when(cardService.getAllCards()).thenReturn(Collections.singletonList(new AdminCardDto()));

    mockMvc
        .perform(get("/cards"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1));
  }

  @Test
  void getAllUserBlockRequest_success() throws Exception {
    setupAdmin();
    when(cardService.getAllUserBlockRequest(1L))
        .thenReturn(Collections.singletonList(new CardBlockRequestDto()));

    mockMvc
        .perform(get("/cards/block/request").param("userId", "1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()").value(1));
  }

  @Test
  void setStatusBlockRequestInProgress_success() throws Exception {
    setupAdmin();
    mockMvc.perform(post("/cards/block/request/1/progress")).andExpect(status().isOk());
  }

  @Test
  void setStatusBlockRequestDone_success() throws Exception {
    setupAdmin();
    mockMvc.perform(post("/cards/block/request/1/done")).andExpect(status().isOk());
  }

  // User tests

  @Test
  void getUserCards_success() throws Exception {
    var user = setupUser();
    var paginationDto = new PaginationDto<UserCardDto>();
    paginationDto.setElements(List.of(new UserCardDto()));
    when(cardService.getUserCards(user, null, 0, 10)).thenReturn(paginationDto);

    mockMvc
        .perform(
            get("/cards/view").principal(SecurityContextHolder.getContext().getAuthentication()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.elements.size()").value(1));
  }

  @Test
  void getUserCards_withSearch() throws Exception {
    var user = setupUser();
    var paginationDto = new PaginationDto<UserCardDto>();
    paginationDto.setElements(List.of(new UserCardDto()));
    when(cardService.getUserCards(user, "search", 0, 10)).thenReturn(paginationDto);

    mockMvc
        .perform(
            get("/cards/view")
                .param("search", "search")
                .principal(SecurityContextHolder.getContext().getAuthentication()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.elements.size()").value(1));
  }

  @Test
  void createCardBlockRequest_success() throws Exception {
    setupUser();
    mockMvc.perform(post("/cards/1/block/request/create")).andExpect(status().isOk());
  }
}
