package com.tresura.dealingservice.services;

import com.tresura.dealingservice.dto.MMDealResponseDTO;
import com.tresura.dealingservice.entities.MMDeal;
import com.tresura.dealingservice.enums.BuySell;
import com.tresura.dealingservice.enums.DealStatus;
import com.tresura.dealingservice.repository.MMDealRepository;
import com.tresura.dealingservice.service.MMDealServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MMDeal Service - Filter Tests")
class MMDealServiceFilterTest {

    @Mock
    private MMDealRepository mmDealRepository;

    @InjectMocks
    private MMDealServiceImpl mmDealService;

    private MMDeal usdDeal;
    private MMDeal inrDeal;

    @BeforeEach
    void setUp() {
        usdDeal = MMDeal.builder()
                .mmDealId(1L).cpId(1L).buySell(BuySell.BORROW)
                .principal(new BigDecimal("1000000.00")).currency("USD")
                .rate(new BigDecimal("5.25"))
                .tradeDate(LocalDate.of(2025, 1, 10))
                .startDate(LocalDate.of(2025, 1, 11))
                .maturityDate(LocalDate.of(2025, 6, 30))
                .status(DealStatus.NEW)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        inrDeal = MMDeal.builder()
                .mmDealId(2L).cpId(2L).buySell(BuySell.LEND)
                .principal(new BigDecimal("5000000.00")).currency("INR")
                .rate(new BigDecimal("7.50"))
                .tradeDate(LocalDate.of(2025, 1, 15))
                .startDate(LocalDate.of(2025, 1, 16))
                .maturityDate(LocalDate.of(2025, 3, 31))
                .status(DealStatus.CONFIRMED)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should return deals filtered by currency")
    void shouldFilterByCurrency() {
        when(mmDealRepository.findByCurrency("USD")).thenReturn(List.of(usdDeal));

        List<MMDealResponseDTO> result = mmDealService.getDealsByCurrency("USD");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCurrency()).isEqualTo("USD");
    }

    @Test
    @DisplayName("Should convert lowercase currency to uppercase when filtering")
    void shouldConvertCurrencyToUppercaseOnFilter() {
        when(mmDealRepository.findByCurrency("USD")).thenReturn(List.of(usdDeal));

        List<MMDealResponseDTO> result = mmDealService.getDealsByCurrency("usd");

        assertThat(result).hasSize(1);
        verify(mmDealRepository).findByCurrency("USD");
    }

    @Test
    @DisplayName("Should return deals filtered by status")
    void shouldFilterByStatus() {
        when(mmDealRepository.findByStatus(DealStatus.CONFIRMED))
                .thenReturn(List.of(inrDeal));

        List<MMDealResponseDTO> result =
                mmDealService.getDealsByStatus(DealStatus.CONFIRMED);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(DealStatus.CONFIRMED);
    }

    @Test
    @DisplayName("Should return deals filtered by BuySell direction")
    void shouldFilterByBuySell() {
        when(mmDealRepository.findByBuySell(BuySell.BORROW))
                .thenReturn(List.of(usdDeal));

        List<MMDealResponseDTO> result =
                mmDealService.getDealsByBuySell(BuySell.BORROW);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBuySell()).isEqualTo(BuySell.BORROW);
    }

    @Test
    @DisplayName("Should return deals in trade date range")
    void shouldReturnDealsInTradeDateRange() {
        LocalDate from = LocalDate.of(2025, 1, 1);
        LocalDate to = LocalDate.of(2025, 1, 31);
        when(mmDealRepository.findByTradeDateBetween(from, to))
                .thenReturn(List.of(usdDeal, inrDeal));

        List<MMDealResponseDTO> result =
                mmDealService.getDealsByTradeDateRange(from, to);

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Should throw when fromDate is after toDate")
    void shouldThrowWhenFromDateAfterToDate() {
        assertThatThrownBy(() -> mmDealService.getDealsByTradeDateRange(
                LocalDate.of(2025, 12, 31),
                LocalDate.of(2025, 1, 1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("fromDate cannot be after toDate");
    }

    @Test
    @DisplayName("Should throw when minAmount is greater than maxAmount")
    void shouldThrowWhenMinAmountGreaterThanMax() {
        assertThatThrownBy(() ->
                mmDealService.getDealsByCurrencyAndPrincipalRange(
                        "USD",
                        new BigDecimal("9000000"),
                        new BigDecimal("1000000")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("minAmount cannot be greater than maxAmount");
    }

    @Test
    @DisplayName("Should return deals by counterparty and status")
    void shouldReturnDealsByCounterpartyAndStatus() {
        when(mmDealRepository.findByCpIdAndStatus(1L, DealStatus.NEW))
                .thenReturn(List.of(usdDeal));

        List<MMDealResponseDTO> result =
                mmDealService.getDealsByCounterpartyAndStatus(1L, DealStatus.NEW);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCpId()).isEqualTo(1L);
    }
}