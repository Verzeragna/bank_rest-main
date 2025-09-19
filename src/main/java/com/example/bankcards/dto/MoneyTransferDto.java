package com.example.bankcards.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MoneyTransferDto {

    @NotBlank
    private String cardFrom;
    @NotBlank
    private String cardTo;
    @NotNull
    private BigDecimal value;
}
