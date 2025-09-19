package com.example.bankcards.controller;

import com.example.bankcards.dto.AdminCardDto;
import com.example.bankcards.dto.CardBlockRequestDto;
import com.example.bankcards.dto.PaginationDto;
import com.example.bankcards.dto.UserCardDto;
import com.example.bankcards.entity.CardBlockRequestStatus;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("cards")
@Tag(name = "Card", description = "Card API")
public class CardController {
  private final CardService cardService;

  @Operation(summary = "Create a new card for a user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully created card"),
      @ApiResponse(responseCode = "400", description = "Invalid input")
  })
  @PreAuthorize("hasAuthority('ADMIN')")
  @PostMapping("/create")
  public void createCardForUser(@Parameter(description = "ID of the user") @NotNull @RequestParam Long userId) {
    cardService.createCardForUser(userId);
  }

  @Operation(summary = "Block a card")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully blocked card"),
      @ApiResponse(responseCode = "404", description = "Card not found")
  })
  @PreAuthorize("hasAuthority('ADMIN')")
  @PostMapping("/{id}/block")
  public void blockCard(@Parameter(description = "ID of the card") @NotNull @PathVariable Long id) {
    cardService.changeCardStatus(id, CardStatus.BLOCKED);
  }

  @Operation(summary = "Activate a card")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully activated card"),
      @ApiResponse(responseCode = "404", description = "Card not found")
  })
  @PreAuthorize("hasAuthority('ADMIN')")
  @PostMapping("/{id}/activate")
  public void activateCard(@Parameter(description = "ID of the card") @NotNull @PathVariable Long id) {
    cardService.changeCardStatus(id, CardStatus.ACTIVE);
  }

  @Operation(summary = "Delete a card")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully deleted card"),
      @ApiResponse(responseCode = "404", description = "Card not found")
  })
  @PreAuthorize("hasAuthority('ADMIN')")
  @DeleteMapping("/{id}")
  public void deleteCard(@Parameter(description = "ID of the card") @NotNull @PathVariable Long id) {
    cardService.deleteCard(id);
  }

  @Operation(summary = "Get all cards")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved cards")
  })
  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping
  public List<AdminCardDto> getAllCards() {
    return cardService.getAllCards();
  }

  @Operation(summary = "Get all block requests for a user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved block requests")
  })
  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping("/block/request")
  public List<CardBlockRequestDto> getAllUserBlockRequest(@Parameter(description = "ID of the user") @NotNull @RequestParam Long userId) {
    return cardService.getAllUserBlockRequest(userId);
  }

  @Operation(summary = "Set block request status to IN_PROGRESS")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully updated status"),
      @ApiResponse(responseCode = "404", description = "Block request not found")
  })
  @PreAuthorize("hasAuthority('ADMIN')")
  @PostMapping("/block/request/{id}/progress")
  public void setStatusBlockRequestInProgress(@Parameter(description = "ID of the block request") @NotNull @PathVariable Long id) {
    cardService.setBlockRequestStatus(id, CardBlockRequestStatus.IN_PROGRESS);
  }

  @Operation(summary = "Set block request status to DONE")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully updated status"),
      @ApiResponse(responseCode = "404", description = "Block request not found")
  })
  @PreAuthorize("hasAuthority('ADMIN')")
  @PostMapping("/block/request/{id}/done")
  public void setStatusBlockRequestDone(@Parameter(description = "ID of the block request") @NotNull @PathVariable Long id) {
    cardService.setBlockRequestStatus(id, CardBlockRequestStatus.DONE);
  }

  @Operation(summary = "Get user cards with pagination")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved user cards")
  })
  @PreAuthorize("hasAuthority('USER')")
  @GetMapping("view")
  public PaginationDto<UserCardDto> getUserCards(
      @AuthenticationPrincipal User user,
      @Parameter(description = "Search term") @RequestParam(required = false) String search,
      @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
      @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
    return cardService.getUserCards(user, search, page, size);
  }

  @Operation(summary = "Create a block request for a card")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully created block request"),
      @ApiResponse(responseCode = "404", description = "Card not found"),
      @ApiResponse(responseCode = "403", description = "Card not owned by user")
  })
  @PreAuthorize("hasAuthority('USER')")
  @PostMapping("/{id}/block/request/create")
  public void createCardBlockRequest(
      @AuthenticationPrincipal User user, @Parameter(description = "ID of the card") @NotNull @PathVariable Long id) {
    cardService.createCardBlockRequest(user, id);
  }
}
