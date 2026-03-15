package com.tresura.dealingservice.dto;

import com.tresura.dealingservice.enums.SecuritiesSide;
import jakarta.validation.constraints.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecuritiesDealRequestDTO {

    @NotNull(message = "Counterparty ID is required")
    private Long cpId;

    @NotNull(message = "Instrument ID is required")
    private Long instrumentId;

    @NotNull(message = "Side is required")
    private SecuritiesSide side;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.0001", message = "Quantity must be greater than 0")
    private BigDecimal quantity;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be positive")
    private BigDecimal price;

    @NotNull(message = "Trade date is required")
    private LocalDate tradeDate;

    @NotNull(message = "Settlement date is required")
    private LocalDate settlementDate;
}