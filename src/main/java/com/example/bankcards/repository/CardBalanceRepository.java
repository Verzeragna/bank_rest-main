package com.example.bankcards.repository;

import com.example.bankcards.entity.CardBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardBalanceRepository extends JpaRepository<CardBalance, Long> {}
