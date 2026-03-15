package com.tresura.dealingservice.services;

import com.tresura.dealingservice.dto.FXDealRequestDTO;
import com.tresura.dealingservice.dto.FXDealResponseDTO;
import com.tresura.dealingservice.entities.FXDeal;
import com.tresura.dealingservice.enums.DealStatus;
import com.tresura.dealingservice.enums.FXDealType;
import com.tresura.dealingservice.exception.ResourceNotFoundException;
import com.tresura.dealingservice.repository.FXDealRepository;
import com.tresura.dealingservice.service.FXDealServiceImpl;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FXDeal Service Tests")
class FXDealServiceTest {

    @Mock
    private FXDealRepository fxDealRepository;

    @InjectMocks
    private FXDealServiceImpl fxDealService;

    private FXDealRequestDTO validRequest;
    private FXDeal savedEntity;
    private FXDeal confirmedEntity;

    @BeforeEach
    void setUp() {
        validRequest = FXDealRequestDTO.builder()
                .cpId(1L)
                .dealType(FXDealType.SPOT)
                .buyCurrency("USD")
                .sellCurrency("INR")
                .buyAmount(new BigDecimal("100000.00"))
                .sellAmount(new BigDecimal("8300000.00"))
                .rate(new BigDecimal("83.00"))
                .tradeDate(LocalDate.of(2025, 1, 1))
                .valueDate(LocalDate.of(2025, 1, 3))
                .build();

        savedEntity = FXDeal.builder()
                .fxDealId(1L)
                .cpId(1L)
                .dealType(FXDealType.SPOT)
                .buyCurrency("USD")
                .sellCurrency("INR")
                .buyAmount(new BigDecimal("100000.00"))
                .sellAmount(new BigDecimal("8300000.00"))
                .rate(new BigDecimal("83.00"))
                .tradeDate(LocalDate.of(2025, 1, 1))
                .valueDate(LocalDate.of(2025, 1, 3))
                .status(DealStatus.NEW)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        confirmedEntity = FXDeal.builder()
                .fxDealId(2L)
                .cpId(1L)
                .dealType(FXDealType.FORWARD)
                .buyCurrency("EUR")
                .sellCurrency("USD")
                .buyAmount(new BigDecimal("50000.00"))
                .sellAmount(new BigDecimal("54000.00"))
                .rate(new BigDecimal("1.08"))
                .tradeDate(LocalDate.of(2025, 1, 1))
                .valueDate(LocalDate.of(2025, 4, 1))
                .status(DealStatus.CONFIRMED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ── Create Tests ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should create FX deal successfully")
    void shouldCreateFXDealSuccessfully() {
        when(fxDealRepository.save(any(FXDeal.class))).thenReturn(savedEntity);

        FXDealResponseDTO result = fxDealService.createDeal(validRequest);

        assertThat(result).isNotNull();
        assertThat(result.getFxDealId()).isEqualTo(1L);
        assertThat(result.getDealType()).isEqualTo(FXDealType.SPOT);
        assertThat(result.getBuyCurrency()).isEqualTo("USD");
        assertThat(result.getSellCurrency()).isEqualTo("INR");
        assertThat(result.getStatus()).isEqualTo(DealStatus.NEW);
        verify(fxDealRepository, times(1)).save(any(FXDeal.class));
    }

    @Test
    @DisplayName("Should throw when buy and sell currency are the same")
    void shouldThrowWhenSameCurrencyPair() {
        validRequest.setSellCurrency("USD"); // same as buyCurrency

        assertThatThrownBy(() -> fxDealService.createDeal(validRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Buy currency and sell currency cannot be the same");

        verify(fxDealRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw when value date is before trade date")
    void shouldThrowWhenValueDateBeforeTradeDate() {
        validRequest.setValueDate(LocalDate.of(2024, 12, 31));

        assertThatThrownBy(() -> fxDealService.createDeal(validRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Value date cannot be before trade date");

        verify(fxDealRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should convert currencies to uppercase on create")
    void shouldConvertCurrenciesToUppercase() {
        validRequest.setBuyCurrency("usd");
        validRequest.setSellCurrency("inr");
        when(fxDealRepository.save(any(FXDeal.class))).thenReturn(savedEntity);

        FXDealResponseDTO result = fxDealService.createDeal(validRequest);

        assertThat(result.getBuyCurrency()).isEqualTo("USD");
        assertThat(result.getSellCurrency()).isEqualTo("INR");
    }

    // ── Read Tests ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return FX deal by ID")
    void shouldReturnFXDealById() {
        when(fxDealRepository.findById(1L)).thenReturn(Optional.of(savedEntity));

        FXDealResponseDTO result = fxDealService.getDealById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getFxDealId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for non-existent FX deal")
    void shouldThrowForNonExistentFXDeal() {
        when(fxDealRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fxDealService.getDealById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("FXDeal");
    }

    @Test
    @DisplayName("Should return all FX deals")
    void shouldReturnAllFXDeals() {
        when(fxDealRepository.findAll()).thenReturn(List.of(savedEntity, confirmedEntity));

        var result = fxDealService.getAllDeals();

        assertThat(result).hasSize(2);
    }

    // ── Update Tests ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should update FX deal when status is NEW")
    void shouldUpdateFXDealSuccessfully() {
        when(fxDealRepository.findById(1L)).thenReturn(Optional.of(savedEntity));
        when(fxDealRepository.save(any(FXDeal.class))).thenReturn(savedEntity);

        FXDealResponseDTO result = fxDealService.updateDeal(1L, validRequest);

        assertThat(result).isNotNull();
        verify(fxDealRepository, times(1)).save(any(FXDeal.class));
    }

    @Test
    @DisplayName("Should throw when updating CONFIRMED FX deal")
    void shouldThrowWhenUpdatingConfirmedFXDeal() {
        when(fxDealRepository.findById(2L)).thenReturn(Optional.of(confirmedEntity));

        assertThatThrownBy(() -> fxDealService.updateDeal(2L, validRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Only deals in NEW status can be updated");
    }

    // ── Delete Tests ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should delete FX deal when status is NEW")
    void shouldDeleteFXDealSuccessfully() {
        when(fxDealRepository.findById(1L)).thenReturn(Optional.of(savedEntity));
        doNothing().when(fxDealRepository).deleteById(1L);

        assertThatNoException().isThrownBy(() -> fxDealService.deleteDeal(1L));
        verify(fxDealRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw when deleting CONFIRMED FX deal")
    void shouldThrowWhenDeletingConfirmedFXDeal() {
        when(fxDealRepository.findById(2L)).thenReturn(Optional.of(confirmedEntity));

        assertThatThrownBy(() -> fxDealService.deleteDeal(2L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Only deals in NEW or CANCELLED status can be deleted");
    }

    // ── Status Transition Tests ───────────────────────────────────────────────

    @Test
    @DisplayName("Should transition FX deal from NEW to CONFIRMED")
    void shouldTransitionNewToConfirmed() {
        FXDeal confirmedResult = FXDeal.builder()
                .fxDealId(1L).cpId(1L).dealType(FXDealType.SPOT)
                .buyCurrency("USD").sellCurrency("INR")
                .buyAmount(BigDecimal.TEN).sellAmount(BigDecimal.TEN)
                .rate(BigDecimal.ONE).tradeDate(LocalDate.now())
                .valueDate(LocalDate.now().plusDays(2))
                .status(DealStatus.CONFIRMED)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        when(fxDealRepository.findById(1L)).thenReturn(Optional.of(savedEntity));
        when(fxDealRepository.save(any(FXDeal.class))).thenReturn(confirmedResult);

        FXDealResponseDTO result = fxDealService.updateStatus(1L, DealStatus.CONFIRMED);

        assertThat(result.getStatus()).isEqualTo(DealStatus.CONFIRMED);
    }

    @Test
    @DisplayName("Should throw on invalid FX status transition NEW to SETTLED")
    void shouldThrowOnInvalidFXStatusTransition() {
        when(fxDealRepository.findById(1L)).thenReturn(Optional.of(savedEntity));

        assertThatThrownBy(() -> fxDealService.updateStatus(1L, DealStatus.SETTLED))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid status transition");
    }

    // ── Filter Tests ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should throw when fromDate is after toDate in trade date range")
    void shouldThrowWhenFromDateAfterToDate() {
        assertThatThrownBy(() -> fxDealService.getDealsByTradeDateRange(
                LocalDate.of(2025, 12, 31),
                LocalDate.of(2025, 1, 1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("fromDate cannot be after toDate");
    }

    @Test
    @DisplayName("Should return FX deals by counterparty")
    void shouldReturnFXDealsByCounterparty() {
        when(fxDealRepository.findByCpId(1L)).thenReturn(List.of(savedEntity));

        var result = fxDealService.getDealsByCounterparty(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCpId()).isEqualTo(1L);
    }
}