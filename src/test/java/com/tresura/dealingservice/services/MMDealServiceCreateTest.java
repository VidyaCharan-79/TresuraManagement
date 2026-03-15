package com.tresura.dealingservice.services;

import com.tresura.dealingservice.dto.MMDealRequestDTO;
import com.tresura.dealingservice.dto.MMDealResponseDTO;
import com.tresura.dealingservice.entities.MMDeal;
import com.tresura.dealingservice.enums.BuySell;
import com.tresura.dealingservice.enums.DealStatus;
import com.tresura.dealingservice.exception.ResourceNotFoundException;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MMDeal Service - Create and Read Tests")
class MMDealServiceCreateTest {

    @Mock
    private MMDealRepository mmDealRepository;

    @InjectMocks
    private MMDealServiceImpl mmDealService;

    private MMDealRequestDTO validRequest;
    private MMDeal savedEntity;

    @BeforeEach
    void setUp() {
        validRequest = MMDealRequestDTO.builder()
                .cpId(1L)
                .buySell(BuySell.BORROW)
                .principal(new BigDecimal("1000000.00"))
                .currency("USD")
                .rate(new BigDecimal("5.25"))
                .tradeDate(LocalDate.of(2025, 1, 1))
                .startDate(LocalDate.of(2025, 1, 2))
                .maturityDate(LocalDate.of(2025, 6, 30))
                .build();

        savedEntity = MMDeal.builder()
                .mmDealId(1L)
                .cpId(1L)
                .buySell(BuySell.BORROW)
                .principal(new BigDecimal("1000000.00"))
                .currency("USD")
                .rate(new BigDecimal("5.25"))
                .tradeDate(LocalDate.of(2025, 1, 1))
                .startDate(LocalDate.of(2025, 1, 2))
                .maturityDate(LocalDate.of(2025, 6, 30))
                .status(DealStatus.NEW)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ── Create Tests ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should create MM deal successfully with valid input")
    void shouldCreateMMDealSuccessfully() {
        when(mmDealRepository.save(any(MMDeal.class))).thenReturn(savedEntity);

        MMDealResponseDTO result = mmDealService.createDeal(validRequest);

        assertThat(result).isNotNull();
        assertThat(result.getMmDealId()).isEqualTo(1L);
        assertThat(result.getCpId()).isEqualTo(1L);
        assertThat(result.getBuySell()).isEqualTo(BuySell.BORROW);
        assertThat(result.getPrincipal()).isEqualByComparingTo("1000000.00");
        assertThat(result.getCurrency()).isEqualTo("USD");
        assertThat(result.getStatus()).isEqualTo(DealStatus.NEW);
        verify(mmDealRepository, times(1)).save(any(MMDeal.class));
    }

    @Test
    @DisplayName("Should convert currency to uppercase on create")
    void shouldConvertCurrencyToUppercase() {
        validRequest.setCurrency("usd");
        when(mmDealRepository.save(any(MMDeal.class))).thenReturn(savedEntity);

        MMDealResponseDTO result = mmDealService.createDeal(validRequest);

        assertThat(result.getCurrency()).isEqualTo("USD");
    }

    @Test
    @DisplayName("Should always set status to NEW on create")
    void shouldSetStatusToNewOnCreate() {
        when(mmDealRepository.save(any(MMDeal.class))).thenReturn(savedEntity);

        MMDealResponseDTO result = mmDealService.createDeal(validRequest);

        assertThat(result.getStatus()).isEqualTo(DealStatus.NEW);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when start date is before trade date")
    void shouldThrowWhenStartDateBeforeTradeDate() {
        validRequest.setStartDate(LocalDate.of(2024, 12, 31)); // before tradeDate

        assertThatThrownBy(() -> mmDealService.createDeal(validRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Start date cannot be before trade date");

        verify(mmDealRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when maturity date is before start date")
    void shouldThrowWhenMaturityDateBeforeStartDate() {
        validRequest.setMaturityDate(LocalDate.of(2025, 1, 1)); // before startDate

        assertThatThrownBy(() -> mmDealService.createDeal(validRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Maturity date cannot be before start date");

        verify(mmDealRepository, never()).save(any());
    }

    // ── Read Tests ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return MM deal when valid ID is provided")
    void shouldReturnDealById() {
        when(mmDealRepository.findById(1L)).thenReturn(Optional.of(savedEntity));

        MMDealResponseDTO result = mmDealService.getDealById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getMmDealId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when ID does not exist")
    void shouldThrowWhenDealNotFound() {
        when(mmDealRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mmDealService.getDealById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("MMDeal")
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Should return all MM deals")
    void shouldReturnAllDeals() {
        when(mmDealRepository.findAll()).thenReturn(java.util.List.of(savedEntity));

        var result = mmDealService.getAllDeals();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMmDealId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should return empty list when no MM deals exist")
    void shouldReturnEmptyListWhenNoDeals() {
        when(mmDealRepository.findAll()).thenReturn(java.util.List.of());

        var result = mmDealService.getAllDeals();

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }
}