package com.example.bankcards.entity;

import com.example.bankcards.exception.CardBalanceException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

@Getter
@Setter
@ToString
@Builder
@RequiredArgsConstructor
@Entity
@Table(name = "card_balance")
@AllArgsConstructor
public class CardBalance {

  @Id
  @Column(name = "card_id")
  private Long cardId;

  @OneToOne
  @MapsId
  @JoinColumn(name = "card_id", nullable = false)
  private Card card;

  @Column(name = "balance", nullable = false, precision = 35, scale = 2)
  private BigDecimal balance;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        CardBalance that = (CardBalance) o;
        return getCardId() != null && Objects.equals(getCardId(), that.getCardId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public void minus(BigDecimal value) {
        if (this.balance.compareTo(value) > -1) {
            this.balance = this.balance.subtract(value);
        } else {
            throw new CardBalanceException();
        }
    }

    public void plus(BigDecimal value) {
        this.balance = this.balance.add(value);
    }
}
