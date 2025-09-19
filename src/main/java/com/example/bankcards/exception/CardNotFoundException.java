package com.example.bankcards.exception;

public class CardNotFoundException extends BankCardsException {
    public CardNotFoundException() {
        super();
    }

    public CardNotFoundException(String message) {
        super(message);
    }
}
