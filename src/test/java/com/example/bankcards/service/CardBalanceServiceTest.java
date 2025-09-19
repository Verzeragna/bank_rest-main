package com.example.bankcards.service;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardBalance;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.CardOwnerException;
import com.example.bankcards.repository.CardBalanceRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.EncryptionCard;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardBalanceServiceTest {

    @InjectMocks
    private CardBalanceService cardBalanceService;

    @Mock
    private EncryptionCard encryptionCard;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private CardBalanceRepository cardBalanceRepository;

    @Test
    void transferMoney_success() {
        var user = new User();
        user.setId(1L);

        var cardFrom = new Card();
        cardFrom.setOwnerId(1L);
        CardBalance balanceFrom = new CardBalance();
        balanceFrom.setBalance(BigDecimal.TEN);
        cardFrom.setBalance(balanceFrom);

        var cardTo = new Card();
        cardTo.setOwnerId(1L);
        var balanceTo = new CardBalance();
        balanceTo.setBalance(BigDecimal.ZERO);
        cardTo.setBalance(balanceTo);

        when(encryptionCard.encrypt("1234")).thenReturn("encrypted1234");
        when(encryptionCard.encrypt("5678")).thenReturn("encrypted5678");
        when(cardRepository.findCardByNumber("encrypted1234")).thenReturn(Optional.of(cardFrom));
        when(cardRepository.findCardByNumber("encrypted5678")).thenReturn(Optional.of(cardTo));

        cardBalanceService.transferMoney(user, "1234", "5678", BigDecimal.ONE);

        verify(cardBalanceRepository, times(2)).save(any(CardBalance.class));
        assertEquals(new BigDecimal("9"), balanceFrom.getBalance());
        assertEquals(new BigDecimal("1"), balanceTo.getBalance());
    }

    @Test
    void transferMoney_cardFromNotFound() {
        var user = new User();
        user.setId(1L);

        when(encryptionCard.encrypt("1234")).thenReturn("encrypted1234");
        when(cardRepository.findCardByNumber("encrypted1234")).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> {
            cardBalanceService.transferMoney(user, "1234", "5678", BigDecimal.ONE);
        });
    }

    @Test
    void transferMoney_cardToNotFound() {
        var user = new User();
        user.setId(1L);

        var cardFrom = new Card();
        cardFrom.setOwnerId(1L);

        when(encryptionCard.encrypt("1234")).thenReturn("encrypted1234");
        when(encryptionCard.encrypt("5678")).thenReturn("encrypted5678");
        when(cardRepository.findCardByNumber("encrypted1234")).thenReturn(Optional.of(cardFrom));
        when(cardRepository.findCardByNumber("encrypted5678")).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> {
            cardBalanceService.transferMoney(user, "1234", "5678", BigDecimal.ONE);
        });
    }

    @Test
    void transferMoney_cardFromNotOwned() {
        var user = new User();
        user.setId(1L);

        var cardFrom = new Card();
        cardFrom.setOwnerId(2L);

        when(encryptionCard.encrypt("1234")).thenReturn("encrypted1234");
        when(cardRepository.findCardByNumber("encrypted1234")).thenReturn(Optional.of(cardFrom));

        assertThrows(CardOwnerException.class, () -> {
            cardBalanceService.transferMoney(user, "1234", "5678", BigDecimal.ONE);
        });
    }

    @Test
    void transferMoney_cardToNotOwned() {
        var user = new User();
        user.setId(1L);

        var cardFrom = new Card();
        cardFrom.setOwnerId(1L);

        var cardTo = new Card();
        cardTo.setOwnerId(2L);

        when(encryptionCard.encrypt("1234")).thenReturn("encrypted1234");
        when(encryptionCard.encrypt("5678")).thenReturn("encrypted5678");
        when(cardRepository.findCardByNumber("encrypted1234")).thenReturn(Optional.of(cardFrom));
        when(cardRepository.findCardByNumber("encrypted5678")).thenReturn(Optional.of(cardTo));

        assertThrows(CardOwnerException.class, () -> {
            cardBalanceService.transferMoney(user, "1234", "5678", BigDecimal.ONE);
        });
    }
}
