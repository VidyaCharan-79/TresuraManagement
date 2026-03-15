package com.tresura.dealingservice.entities;

import com.tresura.dealingservice.enums.BuySell;
import com.tresura.dealingservice.enums.DealStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "mm_deal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MMDeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mm_deal_id")
    private Long mmDealId;

    @Column(name = "cp_id", nullable = false)
    private Long cpId;

    @Enumerated(EnumType.STRING)
    @Column(name = "buy_sell", nullable = false, length = 20)
    private BuySell buySell;

    @Column(name = "principal", nullable = false, precision = 20, scale = 2)
    private BigDecimal principal;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    @Column(name = "rate", nullable = false, precision = 10, scale = 6)
    private BigDecimal rate;

    @Column(name = "trade_date", nullable = false)
    private LocalDate tradeDate;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "maturity_date", nullable = false)
    private LocalDate maturityDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private DealStatus status = DealStatus.NEW;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = DealStatus.NEW;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}