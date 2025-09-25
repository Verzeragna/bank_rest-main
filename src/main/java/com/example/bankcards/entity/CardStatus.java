package com.example.bankcards.entity;

/**
 * Represents the status of a card.
 */
public enum CardStatus {
    /**
     * The card is active and can be used for transactions.
     */
    ACTIVE,
    /**
     * The card is blocked and cannot be used for transactions.
     */
    BLOCKED,
    /**
     * The card has expired.
     */
    EXPIRED
}