package com.example.bankcards.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.bankcards.dto.auth.AuthenticationRequest;
import com.example.bankcards.dto.auth.JwtAuthenticationDto;
import com.example.bankcards.service.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

  private MockMvc mockMvc;

  @Mock private AuthenticationService authenticationService;

  @InjectMocks private AuthenticationController authenticationController;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @BeforeEach
  void setUp() {
    var validator = new LocalValidatorFactoryBean();
    validator.afterPropertiesSet();
    mockMvc =
        MockMvcBuilders.standaloneSetup(authenticationController).setValidator(validator).build();
  }

  @Test
  void authenticate_success() throws Exception {
    var request = new AuthenticationRequest("testuser", "password");
    var expectedDto = new JwtAuthenticationDto("test-token");

    when(authenticationService.authenticate(request)).thenReturn(expectedDto);

    mockMvc
        .perform(
            post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").value("test-token"));
  }

  @Test
  void authenticate_invalidRequest() throws Exception {
    var request = new AuthenticationRequest(null, "password");

    mockMvc
        .perform(
            post("/auth/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest());
  }
}
