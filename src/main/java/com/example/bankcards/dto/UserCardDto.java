package com.example.bankcards.dto;

import com.example.bankcards.entity.CardStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCardDto {
  private String number;
  private LocalDateTime expireDate;
  private CardStatus status;
  private BigDecimal balance;
}
