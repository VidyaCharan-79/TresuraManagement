package com.tresura.dealingservice.dto;

import com.tresura.dealingservice.enums.BuySell;
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
public class MMDealRequestDTO {

    @NotNull(message = "Counterparty ID is required")
    private Long cpId;

    @NotNull(message = "Buy/Sell direction is required")
    private BuySell buySell;

    @NotNull(message = "Principal is required")
    @DecimalMin(value = "0.01", message = "Principal must be greater than 0")
    private BigDecimal principal;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code e.g. USD")
    private String currency;

    @NotNull(message = "Rate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Rate must be positive")
    private BigDecimal rate;

    @NotNull(message = "Trade date is required")
    private LocalDate tradeDate;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "Maturity date is required")
    private LocalDate maturityDate;
}