package com.example.bankcards.util;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EncryptionCard {
  private final String ALGORITHM = "AES";
  private final String TRANSFORMATION = "AES/GCM/NoPadding";
  private final int GCM_TAG_LENGTH = 128;
  private final int IV_LENGTH = 12;

  @Value("${encryption.secret}")
  String secretKey;

  public String encrypt(String input) {
    byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
    SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM);

    byte[] iv = new byte[IV_LENGTH];
    new SecureRandom().nextBytes(iv);

    try {
      var cipher = Cipher.getInstance(TRANSFORMATION);
      GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
      cipher.init(Cipher.ENCRYPT_MODE, keySpec, spec);
      byte[] cipherText = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
      ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
      byteBuffer.put(iv);
      byteBuffer.put(cipherText);
      return Base64.getEncoder().encodeToString(byteBuffer.array());
    } catch (NoSuchAlgorithmException
        | NoSuchPaddingException
        | InvalidAlgorithmParameterException
        | InvalidKeyException
        | IllegalBlockSizeException
        | BadPaddingException e) {
      log.error("Encrypt exception: {}", e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }

  public String decrypt(String encrypted) {
    byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
    SecretKeySpec keySpec = new SecretKeySpec(keyBytes, ALGORITHM);

    byte[] decoded = Base64.getDecoder().decode(encrypted);

    ByteBuffer byteBuffer = ByteBuffer.wrap(decoded);
    byte[] iv = new byte[IV_LENGTH];
    byteBuffer.get(iv);
    byte[] cipherText = new byte[byteBuffer.remaining()];
    byteBuffer.get(cipherText);

    try {
      var cipher = Cipher.getInstance(TRANSFORMATION);
      GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
      cipher.init(Cipher.DECRYPT_MODE, keySpec, spec);
      byte[] plainText = cipher.doFinal(cipherText);
      return new String(plainText, StandardCharsets.UTF_8);
    } catch (NoSuchAlgorithmException
        | NoSuchPaddingException
        | InvalidKeyException
        | InvalidAlgorithmParameterException
        | IllegalBlockSizeException
        | BadPaddingException e) {
      log.error("Decrypt exception: {}", e.getMessage(), e);
      throw new RuntimeException(e);
    }
  }
}
