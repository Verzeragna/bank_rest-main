package com.example.bankcards.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.bankcards.dto.AdminCardDto;
import com.example.bankcards.dto.CardBlockRequestDto;
import com.example.bankcards.dto.PaginationDto;
import com.example.bankcards.dto.UserCardDto;
import com.example.bankcards.entity.*;
import com.example.bankcards.exception.CardBlockRequestNotFoundException;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.CardOwnerException;
import com.example.bankcards.mapper.CardBlockRequestMapper;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardBlockRequestRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.EncryptionCard;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @InjectMocks
    private CardService cardService;

    @Mock
    private CardMapper cardMapper;

    @Mock
    private CardBlockRequestMapper cardBlockRequestMapper;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private EncryptionCard encryptionCard;

    @Mock
    private CardBlockRequestRepository cardBlockRequestRepository;

    @Test
    void blockUserCards() {
        var user = new User();
        var card = new Card();
        card.setStatus(CardStatus.ACTIVE);
        user.setCards(Collections.singletonList(card));

        cardService.blockUserCards(user);

        assertEquals(CardStatus.BLOCKED, card.getStatus());
    }

    @Test
    void createCardForUser() {
        when(encryptionCard.encrypt(anyString())).thenReturn("encryptedCardNumber");
        when(cardRepository.findCardByNumber(anyString())).thenReturn(Optional.empty());

        cardService.createCardForUser(1L);

        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void changeCardStatus_success() {
        var card = new Card();
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        cardService.changeCardStatus(1L, CardStatus.BLOCKED);

        verify(cardRepository).save(card);
        assertEquals(CardStatus.BLOCKED, card.getStatus());
    }

    @Test
    void changeCardStatus_cardNotFound() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> {
            cardService.changeCardStatus(1L, CardStatus.BLOCKED);
        });
    }

    @Test
    void deleteCard_success() {
        var card = new Card();
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        cardService.deleteCard(1L);

        verify(cardRepository).delete(card);
    }

    @Test
    void deleteCard_cardNotFound() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> {
            cardService.deleteCard(1L);
        });
    }

    @Test
    void getAllCards() {
        var card = new Card();
        card.setNumber("encryptedNumber");
        when(cardRepository.findAll()).thenReturn(Collections.singletonList(card));
        when(encryptionCard.decrypt("encryptedNumber")).thenReturn("decryptedNumber");
        when(cardMapper.toAdminCardDto(card)).thenReturn(new AdminCardDto());

        var result = cardService.getAllCards();

        assertEquals(1, result.size());
        assertEquals("decryptedNumber", card.getNumber());
    }

    @Test
    void getUserCards_noSearch() {
        var user = new User();
        user.setId(1L);
        var card = new Card();
        card.setNumber("encryptedNumber");
        var page = new PageImpl<>(Collections.singletonList(card));
        when(cardRepository.findByOwnerId(eq(1L), any(PageRequest.class))).thenReturn(page);
        when(encryptionCard.decrypt("encryptedNumber")).thenReturn("decryptedNumber");
        when(cardMapper.toUserCardDto(card)).thenReturn(new UserCardDto());

        var result = cardService.getUserCards(user, null, 0, 10);

        assertEquals(1, result.getElements().size());
    }

    @Test
    void getUserCards_withSearch() {
        var user = new User();
        user.setId(1L);
        var card = new Card();
        card.setNumber("encryptedNumber");
        var page = new PageImpl<>(Collections.singletonList(card));
        when(encryptionCard.encrypt("search")).thenReturn("encryptedSearch");
        when(cardRepository.findByOwnerIdAndNumberContaining(eq(1L), eq("encryptedSearch"), any(PageRequest.class))).thenReturn(page);
        when(encryptionCard.decrypt("encryptedNumber")).thenReturn("decryptedNumber");
        when(cardMapper.toUserCardDto(card)).thenReturn(new UserCardDto());

        var result = cardService.getUserCards(user, "search", 0, 10);

        assertEquals(1, result.getElements().size());
    }

    @Test
    void createCardBlockRequest_success() {
        var user = new User();
        user.setId(1L);
        user.setName("name");
        user.setLastName("lastName");
        user.setSurname("surname");
        var card = new Card();
        card.setOwnerId(1L);
        card.setNumber("encryptedNumber");
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(encryptionCard.decrypt("encryptedNumber")).thenReturn("1234567812345678");

        cardService.createCardBlockRequest(user, 1L);

        verify(cardBlockRequestRepository).save(any(CardBlockRequest.class));
    }

    @Test
    void createCardBlockRequest_cardNotFound() {
        var user = new User();
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> {
            cardService.createCardBlockRequest(user, 1L);
        });
    }

    @Test
    void createCardBlockRequest_cardNotOwned() {
        var user = new User();
        user.setId(1L);
        var card = new Card();
        card.setOwnerId(2L);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));

        assertThrows(CardOwnerException.class, () -> {
            cardService.createCardBlockRequest(user, 1L);
        });
    }

    @Test
    void getAllUserBlockRequest() {
        var request = new CardBlockRequest();
        when(cardBlockRequestRepository.findAllByUserId(1L)).thenReturn(Collections.singletonList(request));
        when(cardBlockRequestMapper.toDto(request)).thenReturn(new CardBlockRequestDto());

        var result = cardService.getAllUserBlockRequest(1L);

        assertEquals(1, result.size());
    }

    @Test
    void setBlockRequestStatus_success() {
        var request = new CardBlockRequest();
        when(cardBlockRequestRepository.findById(1L)).thenReturn(Optional.of(request));

        cardService.setBlockRequestStatus(1L, CardBlockRequestStatus.DONE);

        verify(cardBlockRequestRepository).save(request);
        assertEquals(CardBlockRequestStatus.DONE, request.getStatus());
    }

    @Test
    void setBlockRequestStatus_requestNotFound() {
        when(cardBlockRequestRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CardBlockRequestNotFoundException.class, () -> {
            cardService.setBlockRequestStatus(1L, CardBlockRequestStatus.DONE);
        });
    }
}
