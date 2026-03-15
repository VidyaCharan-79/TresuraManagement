package com.tresura.dealingservice.dto;

import com.tresura.dealingservice.enums.FXDealType;
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
public class FXDealRequestDTO {

    @NotNull(message = "Counterparty ID is required")
    private Long cpId;

    @NotNull(message = "Deal type is required")
    private FXDealType dealType;

    @NotBlank(message = "Buy currency is required")
    @Size(min = 3, max = 3, message = "Buy currency must be a 3-letter ISO code e.g. USD")
    private String buyCurrency;

    @NotBlank(message = "Sell currency is required")
    @Size(min = 3, max = 3, message = "Sell currency must be a 3-letter ISO code e.g. INR")
    private String sellCurrency;

    @NotNull(message = "Buy amount is required")
    @DecimalMin(value = "0.01", message = "Buy amount must be greater than 0")
    private BigDecimal buyAmount;

    @NotNull(message = "Sell amount is required")
    @DecimalMin(value = "0.01", message = "Sell amount must be greater than 0")
    private BigDecimal sellAmount;

    @NotNull(message = "Rate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Rate must be positive")
    private BigDecimal rate;

    @NotNull(message = "Trade date is required")
    private LocalDate tradeDate;

    @NotNull(message = "Value date is required")
    private LocalDate valueDate;
}