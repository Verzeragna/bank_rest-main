package com.example.bankcards.exception;

public class CardOwnerException extends BankCardsException {
    public CardOwnerException() {
        super();
    }

    public CardOwnerException(String message) {
        super(message);
    }
}
