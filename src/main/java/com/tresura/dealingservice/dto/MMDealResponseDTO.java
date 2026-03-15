package com.tresura.dealingservice.dto;

import com.tresura.dealingservice.enums.BuySell;
import com.tresura.dealingservice.enums.DealStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MMDealResponseDTO {

    private Long mmDealId;
    private Long cpId;
    private BuySell buySell;
    private BigDecimal principal;
    private String currency;
    private BigDecimal rate;
    private LocalDate tradeDate;
    private LocalDate startDate;
    private LocalDate maturityDate;
    private DealStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}