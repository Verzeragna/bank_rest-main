package com.example.bankcards.exception;

public class BankCardsException extends RuntimeException {
    public BankCardsException() {
        super();
    }

    public BankCardsException(String message) {
        super(message);
    }
}
