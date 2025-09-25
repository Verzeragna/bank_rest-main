package com.example.bankcards.service;

import com.example.bankcards.dto.AdminCardDto;
import com.example.bankcards.dto.CardBlockRequestDto;
import com.example.bankcards.dto.PaginationDto;
import com.example.bankcards.dto.UserCardDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBalance;
import com.example.bankcards.entity.CardBlockRequest;
import com.example.bankcards.entity.CardBlockRequestStatus;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardBlockRequestNotFoundException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.CardOwnerException;
import com.example.bankcards.mapper.CardBlockRequestMapper;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardBlockRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.EncryptionCard;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CardService {
  private final CardMapper cardMapper;
    private final CardBlockRequestMapper cardBlockRequestMapper;
  private final CardRepository cardRepository;
  private final EncryptionCard encryptionCard;
  private final CardBlockRequestRepository cardBlockRequestRepository;

  /**
   * Blocks all cards associated with a user.
   *
   * @param user The user whose cards are to be blocked.
   */
  @Transactional
  public void blockUserCards(User user) {
    user.getCards().forEach(card -> card.setStatus(CardStatus.BLOCKED));
  }

  /**
   * Creates a new card for a user.
   *
   * @param userId The ID of the user for whom the card is to be created.
   */
  @Transactional
  public void createCardForUser(long userId) {
    var card =
        Card.builder()
            .number(encryptionCard.encrypt(generateCardNumber()))
            .expireDate(LocalDateTime.now().plusYears(3))
            .createdAt(LocalDateTime.now())
            .status(CardStatus.ACTIVE)
            .ownerId(userId)
            .build();

    var balance = CardBalance.builder().balance(BigDecimal.ZERO).card(card).build();

    card.setBalance(balance);

    cardRepository.save(card);
  }

  private String generateCardNumber() {
    var generate = true;
    var cardNumber = new StringBuilder();
    while (generate) {
      for (int i = 0; i < 4; i++) {
        var number = ThreadLocalRandom.current().nextInt(1000, 9999);
        cardNumber.append(number);
      }
      var cardDb = cardRepository.findCardByNumber(cardNumber.toString());
      if (cardDb.isEmpty()) {
        generate = false;
      } else {
        cardNumber.setLength(0);
      }
    }
    return cardNumber.toString();
  }

  /**
   * Changes the status of a card.
   *
   * @param id The ID of the card to update.
   * @param status The new status of the card.
   * @throws CardNotFoundException if the card with the given ID is not found.
   */
  @Transactional
  public void changeCardStatus(Long id, CardStatus status) {
    cardRepository
        .findById(id)
        .ifPresentOrElse(
            card -> {
              card.setStatus(status);
              cardRepository.save(card);
            },
            () -> {
              throw new CardNotFoundException(String.valueOf(id));
            });
  }

  /**
   * Deletes a card by its ID.
   *
   * @param id The ID of the card to delete.
   * @throws CardNotFoundException if the card with the given ID is not found.
   */
  @Transactional
  public void deleteCard(Long id) {
    cardRepository
        .findById(id)
        .ifPresentOrElse(
            cardRepository::delete,
            () -> {
              throw new CardNotFoundException(String.valueOf(id));
            });
  }

  /**
   * Retrieves a list of all cards.
   *
   * @return A list of DTOs representing all cards.
   */
  public List<AdminCardDto> getAllCards() {
    var cards = cardRepository.findAll();
    cards.forEach(card -> card.setNumber(encryptionCard.decrypt(card.getNumber())));
    return cards.stream().map(cardMapper::toAdminCardDto).toList();
  }

  /**
   * Retrieves a paginated list of cards for a specific user.
   *
   * @param user The user whose cards to retrieve.
   * @param search A search string to filter cards by number.
   * @param page The page number to retrieve.
   * @param size The number of elements per page.
   * @return A pagination DTO containing the user's cards.
   */
  public PaginationDto<UserCardDto> getUserCards(User user, String search, int page, int size) {
    Page<Card> cardsPage;
    var pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
    if (search != null && !search.isBlank()) {
      var encryptedSearch = encryptionCard.encrypt(search);
      cardsPage =
          cardRepository.findByOwnerIdAndNumberContaining(user.getId(), encryptedSearch, pageable);
    } else {
      cardsPage = cardRepository.findByOwnerId(user.getId(), pageable);
    }
    cardsPage
        .getContent()
        .forEach(card -> card.setNumber(encryptionCard.decrypt(card.getNumber())));
    var result = new PaginationDto<UserCardDto>();
    result.setTotalPages(cardsPage.getTotalPages());
    result.setTotalElements(cardsPage.getTotalElements());
    result.setElements(cardsPage.getContent().stream().map(cardMapper::toUserCardDto).toList());
    return result;
  }

  /**
   * Creates a request to block a card.
   *
   * @param user The user requesting the block.
   * @param cardId The ID of the card to be blocked.
   * @throws CardNotFoundException if the card with the given ID is not found.
   * @throws CardOwnerException if the user does not own the card.
   */
  @Transactional
  public void createCardBlockRequest(User user, Long cardId) {
    var cardOpt = cardRepository.findById(cardId);
    if (cardOpt.isEmpty()) {
      throw new CardNotFoundException(String.valueOf(cardId));
    }
    var card = cardOpt.get();
    if (!card.getOwnerId().equals(user.getId())) {
      throw new CardOwnerException(String.valueOf(cardId));
    }
    card.setNumber(encryptionCard.decrypt(card.getNumber()));
    var userName =
        user.getSurname().isBlank()
            ? user.getName().concat(user.getLastName())
            : user.getSurname()
                .concat(" ")
                .concat(user.getName())
                .concat(" ")
                .concat(user.getLastName());
    var cardBlockRequest =
        CardBlockRequest.builder()
            .cardId(cardId)
            .cardNumber(card.hideNumber())
            .userId(user.getId())
            .userName(userName)
            .status(CardBlockRequestStatus.CREATED)
            .build();
    cardBlockRequestRepository.save(cardBlockRequest);
  }

  /**
   * Retrieves all card block requests for a specific user.
   *
   * @param userId The ID of the user.
   * @return A list of DTOs representing the user's card block requests.
   */
  public List<CardBlockRequestDto> getAllUserBlockRequest(Long userId) {
    return cardBlockRequestRepository.findAllByUserId(userId)
            .stream()
            .map(cardBlockRequestMapper::toDto)
            .toList();
  }

  /**
   * Sets the status of a card block request.
   *
   * @param id The ID of the card block request.
   * @param status The new status.
   * @throws CardBlockRequestNotFoundException if the card block request with the given ID is not found.
   */
  public void setBlockRequestStatus(Long id, CardBlockRequestStatus status) {
    cardBlockRequestRepository
        .findById(id)
        .ifPresentOrElse(
            request -> {
              request.setStatus(status);
              cardBlockRequestRepository.save(request);
            },
            () -> {
              throw new CardBlockRequestNotFoundException();
            });
  }
}