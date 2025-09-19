package com.example.bankcards.dto;

import com.example.bankcards.entity.CardBlockRequestStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CardBlockRequestDto {
    private Long id;
    private Long userId;
    private String userName;
    private Long cardId;
    private String cardNumber;
    private LocalDateTime createdAt;
    private CardBlockRequestStatus status;
}
