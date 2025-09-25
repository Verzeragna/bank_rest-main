package com.example.bankcards.entity;

/**
 * Represents the status of a card block request.
 */
public enum CardBlockRequestStatus {
    /**
     * The request has been created but not yet processed.
     */
    CREATED,
    /**
     * The request is currently being processed.
     */
    IN_PROGRESS,
    /**
     * The request has been completed.
     */
    DONE
}