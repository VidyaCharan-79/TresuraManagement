package com.tresura.dealingservice.dto;

import com.tresura.dealingservice.enums.DealStatus;
import com.tresura.dealingservice.enums.FXDealType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FXDealResponseDTO {

    private Long fxDealId;
    private Long cpId;
    private FXDealType dealType;
    private String buyCurrency;
    private String sellCurrency;
    private BigDecimal buyAmount;
    private BigDecimal sellAmount;
    private BigDecimal rate;
    private LocalDate tradeDate;
    private LocalDate valueDate;
    private DealStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}