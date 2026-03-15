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
@DisplayName("MMDeal Service - Update, Delete and Status Tests")
class MMDealServiceUpdateDeleteTest {

    @Mock
    private MMDealRepository mmDealRepository;

    @InjectMocks
    private MMDealServiceImpl mmDealService;

    private MMDeal newDeal;
    private MMDeal confirmedDeal;
    private MMDeal settledDeal;
    private MMDealRequestDTO updateRequest;

    @BeforeEach
    void setUp() {
        newDeal = MMDeal.builder()
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

        confirmedDeal = MMDeal.builder()
                .mmDealId(2L)
                .cpId(1L)
                .buySell(BuySell.LEND)
                .principal(new BigDecimal("500000.00"))
                .currency("INR")
                .rate(new BigDecimal("7.50"))
                .tradeDate(LocalDate.of(2025, 1, 1))
                .startDate(LocalDate.of(2025, 1, 2))
                .maturityDate(LocalDate.of(2025, 3, 31))
                .status(DealStatus.CONFIRMED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        settledDeal = MMDeal.builder()
                .mmDealId(3L)
                .status(DealStatus.SETTLED)
                .cpId(1L)
                .buySell(BuySell.BORROW)
                .principal(BigDecimal.TEN)
                .currency("USD")
                .rate(BigDecimal.ONE)
                .tradeDate(LocalDate.now())
                .startDate(LocalDate.now())
                .maturityDate(LocalDate.now().plusMonths(1))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        updateRequest = MMDealRequestDTO.builder()
                .cpId(2L)
                .buySell(BuySell.LEND)
                .principal(new BigDecimal("2000000.00"))
                .currency("EUR")
                .rate(new BigDecimal("4.50"))
                .tradeDate(LocalDate.of(2025, 1, 1))
                .startDate(LocalDate.of(2025, 1, 2))
                .maturityDate(LocalDate.of(2025, 9, 30))
                .build();
    }

    // ── Update Tests ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should update MM deal successfully when status is NEW")
    void shouldUpdateDealSuccessfully() {
        when(mmDealRepository.findById(1L)).thenReturn(Optional.of(newDeal));
        when(mmDealRepository.save(any(MMDeal.class))).thenReturn(newDeal);

        MMDealResponseDTO result = mmDealService.updateDeal(1L, updateRequest);

        assertThat(result).isNotNull();
        verify(mmDealRepository, times(1)).save(any(MMDeal.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when updating CONFIRMED deal")
    void shouldThrowWhenUpdatingConfirmedDeal() {
        when(mmDealRepository.findById(2L)).thenReturn(Optional.of(confirmedDeal));

        assertThatThrownBy(() -> mmDealService.updateDeal(2L, updateRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Only deals in NEW status can be updated");

        verify(mmDealRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent deal")
    void shouldThrowWhenUpdatingNonExistentDeal() {
        when(mmDealRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mmDealService.updateDeal(99L, updateRequest))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── Delete Tests ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should delete MM deal successfully when status is NEW")
    void shouldDeleteNewDealSuccessfully() {
        when(mmDealRepository.findById(1L)).thenReturn(Optional.of(newDeal));
        doNothing().when(mmDealRepository).deleteById(1L);

        assertThatNoException().isThrownBy(() -> mmDealService.deleteDeal(1L));
        verify(mmDealRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when deleting SETTLED deal")
    void shouldThrowWhenDeletingSettledDeal() {
        when(mmDealRepository.findById(3L)).thenReturn(Optional.of(settledDeal));

        assertThatThrownBy(() -> mmDealService.deleteDeal(3L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Only deals in NEW or CANCELLED status can be deleted");

        verify(mmDealRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent deal")
    void shouldThrowWhenDeletingNonExistentDeal() {
        when(mmDealRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mmDealService.deleteDeal(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // ── Status Transition Tests ───────────────────────────────────────────────

    @Test
    @DisplayName("Should transition status from NEW to CONFIRMED")
    void shouldTransitionFromNewToConfirmed() {
        MMDeal confirmedResult = MMDeal.builder()
                .mmDealId(1L).cpId(1L).buySell(BuySell.BORROW)
                .principal(BigDecimal.TEN).currency("USD").rate(BigDecimal.ONE)
                .tradeDate(LocalDate.now()).startDate(LocalDate.now())
                .maturityDate(LocalDate.now().plusMonths(1))
                .status(DealStatus.CONFIRMED)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        when(mmDealRepository.findById(1L)).thenReturn(Optional.of(newDeal));
        when(mmDealRepository.save(any(MMDeal.class))).thenReturn(confirmedResult);

        MMDealResponseDTO result = mmDealService.updateStatus(1L, DealStatus.CONFIRMED);

        assertThat(result.getStatus()).isEqualTo(DealStatus.CONFIRMED);
    }

    @Test
    @DisplayName("Should transition status from NEW to CANCELLED")
    void shouldTransitionFromNewToCancelled() {
        MMDeal cancelledResult = MMDeal.builder()
                .mmDealId(1L).cpId(1L).buySell(BuySell.BORROW)
                .principal(BigDecimal.TEN).currency("USD").rate(BigDecimal.ONE)
                .tradeDate(LocalDate.now()).startDate(LocalDate.now())
                .maturityDate(LocalDate.now().plusMonths(1))
                .status(DealStatus.CANCELLED)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        when(mmDealRepository.findById(1L)).thenReturn(Optional.of(newDeal));
        when(mmDealRepository.save(any(MMDeal.class))).thenReturn(cancelledResult);

        MMDealResponseDTO result = mmDealService.updateStatus(1L, DealStatus.CANCELLED);

        assertThat(result.getStatus()).isEqualTo(DealStatus.CANCELLED);
    }

    @Test
    @DisplayName("Should transition status from CONFIRMED to SETTLED")
    void shouldTransitionFromConfirmedToSettled() {
        MMDeal settledResult = MMDeal.builder()
                .mmDealId(2L).cpId(1L).buySell(BuySell.LEND)
                .principal(BigDecimal.TEN).currency("INR").rate(BigDecimal.ONE)
                .tradeDate(LocalDate.now()).startDate(LocalDate.now())
                .maturityDate(LocalDate.now().plusMonths(1))
                .status(DealStatus.SETTLED)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        when(mmDealRepository.findById(2L)).thenReturn(Optional.of(confirmedDeal));
        when(mmDealRepository.save(any(MMDeal.class))).thenReturn(settledResult);

        MMDealResponseDTO result = mmDealService.updateStatus(2L, DealStatus.SETTLED);

        assertThat(result.getStatus()).isEqualTo(DealStatus.SETTLED);
    }

    @Test
    @DisplayName("Should throw when transitioning from NEW to SETTLED directly")
    void shouldThrowWhenInvalidTransitionNewToSettled() {
        when(mmDealRepository.findById(1L)).thenReturn(Optional.of(newDeal));

        assertThatThrownBy(() -> mmDealService.updateStatus(1L, DealStatus.SETTLED))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid status transition");
    }

    @Test
    @DisplayName("Should throw when transitioning from SETTLED to any status")
    void shouldThrowWhenTransitioningFromSettled() {
        when(mmDealRepository.findById(3L)).thenReturn(Optional.of(settledDeal));

        assertThatThrownBy(() -> mmDealService.updateStatus(3L, DealStatus.CONFIRMED))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid status transition");
    }
}