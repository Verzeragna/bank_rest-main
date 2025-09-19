package com.example.bankcards.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EncryptionCardTest {

    private EncryptionCard encryptionCard;

    @BeforeEach
    void setUp() {
        encryptionCard = new EncryptionCard();
        encryptionCard.secretKey = "12345678901234567890123456789012"; // 32-byte key
    }

    @Test
    void encrypt_decrypt_success() {
        String originalInput = "test input";
        String encrypted = encryptionCard.encrypt(originalInput);
        String decrypted = encryptionCard.decrypt(encrypted);
        assertEquals(originalInput, decrypted);
    }

    @Test
    void decrypt_invalidInput() {
        assertThrows(RuntimeException.class, () -> encryptionCard.decrypt("invalid input"));
    }
}
