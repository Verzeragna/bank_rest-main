package com.example.bankcards.controller;

import com.example.bankcards.dto.MoneyTransferDto;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.CardBalanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("balance")
@Tag(name = "Card Balance", description = "Card Balance API")
public class CardBalanceController {
    private final CardBalanceService cardBalanceService;

    @Operation(summary = "Transfer money between cards")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully transferred money"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Card not found"),
        @ApiResponse(responseCode = "403", description = "Card not owned by user")
    })
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("transfer")
    public void transferMoney(@AuthenticationPrincipal(expression = "user") User user,
                              @Valid @RequestBody MoneyTransferDto dto) {
        cardBalanceService.transferMoney(user, dto.getCardFrom(), dto.getCardTo(), dto.getValue());
    }
}
