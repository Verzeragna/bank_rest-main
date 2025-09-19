package com.example.bankcards.service;

import com.example.bankcards.entity.User;
import com.example.bankcards.exception.CardNotFoundException;
import com.example.bankcards.exception.CardOwnerException;
import com.example.bankcards.repository.CardBalanceRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.util.EncryptionCard;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CardBalanceService {
    private final EncryptionCard encryptionCard;
    private final CardRepository cardRepository;
    private final CardBalanceRepository cardBalanceRepository;

    @Transactional
    public void transferMoney(User user, String cardFrom, String cardTo, BigDecimal value) {
        var cardFromDbOpt = cardRepository.findCardByNumber(encryptionCard.encrypt(cardFrom));
        if (cardFromDbOpt.isEmpty()) {
            throw new CardNotFoundException(cardFrom);
        }
        var cardFromDb = cardFromDbOpt.get();
        if (!cardFromDb.getOwnerId().equals(user.getId())) {
            throw new CardOwnerException(cardFrom);
        }
        var cardToDbOpt = cardRepository.findCardByNumber(encryptionCard.encrypt(cardTo));
        if (cardToDbOpt.isEmpty()) {
            throw new CardNotFoundException(cardTo);
        }
        var cardToDb = cardToDbOpt.get();
        if (!cardToDb.getOwnerId().equals(user.getId())) {
            throw new CardOwnerException(cardTo);
        }
        cardFromDb.getBalance().minus(value);
        cardToDb.getBalance().plus(value);
        cardBalanceRepository.save(cardFromDb.getBalance());
        cardBalanceRepository.save(cardToDb.getBalance());
    }
}
