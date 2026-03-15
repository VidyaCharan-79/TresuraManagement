package com.tresura.dealingservice.dto;

import com.tresura.dealingservice.enums.DealStatus;
import com.tresura.dealingservice.enums.SecuritiesSide;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecuritiesDealResponseDTO {

    private Long secDealId;
    private Long cpId;
    private Long instrumentId;
    private SecuritiesSide side;
    private BigDecimal quantity;
    private BigDecimal price;
    private BigDecimal notionalValue;       // calculated: quantity × price
    private LocalDate tradeDate;
    private LocalDate settlementDate;
    private DealStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
