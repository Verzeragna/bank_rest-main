package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class AdminCardDto {
  private Long id;
  private String number;
  private LocalDateTime expireDate;
  private LocalDateTime createdAt;
  private CardStatus status;
  private BigDecimal balance;
  private Long ownerId;
}
