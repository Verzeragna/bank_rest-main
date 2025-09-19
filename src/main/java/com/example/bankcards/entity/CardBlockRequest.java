package com.example.bankcards.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@Entity
@Table(name = "card_block_request", uniqueConstraints = {
        @UniqueConstraint(name = "uq_user_card", columnNames = {"user_id", "card_id"})
})
@AllArgsConstructor
public class CardBlockRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_name", nullable = false, length = 150)
    private String userName;

    @Column(name = "card_id", nullable = false)
    private Long cardId;

    @Column(name = "card_number", nullable = false, length = 16)
    private String cardNumber;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", nullable = false)
    private CardBlockRequestStatus status;
}
