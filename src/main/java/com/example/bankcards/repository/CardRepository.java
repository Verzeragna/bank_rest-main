package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

  Optional<Card> findCardByNumber(String number);

  Page<Card> findByOwnerId(Long ownerId, Pageable pageable);

  Page<Card> findByOwnerIdAndNumberContaining(Long ownerId, String number, Pageable pageable);
}
