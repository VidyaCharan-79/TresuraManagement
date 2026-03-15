package com.tresura.dealingservice.services;

import com.tresura.dealingservice.dto.SecuritiesDealRequestDTO;
import com.tresura.dealingservice.dto.SecuritiesDealResponseDTO;
import com.tresura.dealingservice.entities.SecuritiesDeal;
import com.tresura.dealingservice.enums.DealStatus;
import com.tresura.dealingservice.enums.SecuritiesSide;
import com.tresura.dealingservice.exception.ResourceNotFoundException;
import com.tresura.dealingservice.repository.SecuritiesDealRepository;
import com.tresura.dealingservice.service.SecuritiesDealServiceImpl;
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
@DisplayName("SecuritiesDeal Service Tests")
class SecuritiesDealServiceTest {

    @Mock
    private SecuritiesDealRepository securitiesDealRepository;

    @InjectMocks
    private SecuritiesDealServiceImpl securitiesDealService;

    private SecuritiesDealRequestDTO validRequest;
    private SecuritiesDeal savedEntity;
    private SecuritiesDeal confirmedEntity;

    @BeforeEach
    void setUp() {
        validRequest = SecuritiesDealRequestDTO.builder()
                .cpId(1L)
                .instrumentId(10L)
                .side(SecuritiesSide.BUY)
                .quantity(new BigDecimal("1000.0000"))
                .price(new BigDecimal("98.50"))
                .tradeDate(LocalDate.of(2025, 1, 1))
                .settlementDate(LocalDate.of(2025, 1, 3))
                .build();

        savedEntity = SecuritiesDeal.builder()
                .secDealId(1L)
                .cpId(1L)
                .instrumentId(10L)
                .side(SecuritiesSide.BUY)
                .quantity(new BigDecimal("1000.0000"))
                .price(new BigDecimal("98.50"))
                .tradeDate(LocalDate.of(2025, 1, 1))
                .settlementDate(LocalDate.of(2025, 1, 3))
                .status(DealStatus.NEW)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        confirmedEntity = SecuritiesDeal.builder()
                .secDealId(2L)
                .cpId(2L)
                .instrumentId(20L)
                .side(SecuritiesSide.REPO)
                .quantity(new BigDecimal("5000.0000"))
                .price(new BigDecimal("100.00"))
                .tradeDate(LocalDate.of(2025, 1, 1))
                .settlementDate(LocalDate.of(2025, 1, 3))
                .status(DealStatus.CONFIRMED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // ── Create Tests ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should create securities deal successfully")
    void shouldCreateSecuritiesDealSuccessfully() {
        when(securitiesDealRepository.save(any(SecuritiesDeal.class)))
                .thenReturn(savedEntity);

        SecuritiesDealResponseDTO result =
                securitiesDealService.createDeal(validRequest);

        assertThat(result).isNotNull();
        assertThat(result.getSecDealId()).isEqualTo(1L);
        assertThat(result.getSide()).isEqualTo(SecuritiesSide.BUY);
        assertThat(result.getStatus()).isEqualTo(DealStatus.NEW);
        verify(securitiesDealRepository, times(1)).save(any(SecuritiesDeal.class));
    }

    @Test
    @DisplayName("Should calculate notional value as quantity x price")
    void shouldCalculateNotionalValue() {
        when(securitiesDealRepository.save(any(SecuritiesDeal.class)))
                .thenReturn(savedEntity);

        SecuritiesDealResponseDTO result =
                securitiesDealService.createDeal(validRequest);

        // 1000 * 98.50 = 98500.00
        BigDecimal expectedNotional = new BigDecimal("1000.0000")
                .multiply(new BigDecimal("98.50"));
        assertThat(result.getNotionalValue())
                .isEqualByComparingTo(expectedNotional);
    }

    @Test
    @DisplayName("Should throw when settlement date is before trade date")
    void shouldThrowWhenSettlementDateBeforeTradeDate() {
        validRequest.setSettlementDate(LocalDate.of(2024, 12, 31));

        assertThatThrownBy(() -> securitiesDealService.createDeal(validRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Settlement date cannot be before trade date");

        verify(securitiesDealRepository, never()).save(any());
    }

    // ── Read Tests ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return securities deal by ID")
    void shouldReturnSecuritiesDealById() {
        when(securitiesDealRepository.findById(1L))
                .thenReturn(Optional.of(savedEntity));

        SecuritiesDealResponseDTO result =
                securitiesDealService.getDealById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getSecDealId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for non-existent deal")
    void shouldThrowForNonExistentDeal() {
        when(securitiesDealRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> securitiesDealService.getDealById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("SecuritiesDeal");
    }

    @Test
    @DisplayName("Should return all securities deals")
    void shouldReturnAllDeals() {
        when(securitiesDealRepository.findAll())
                .thenReturn(List.of(savedEntity, confirmedEntity));

        var result = securitiesDealService.getAllDeals();

        assertThat(result).hasSize(2);
    }

    // ── Update and Delete Tests ───────────────────────────────────────────────

    @Test
    @DisplayName("Should update securities deal when status is NEW")
    void shouldUpdateSuccessfully() {
        when(securitiesDealRepository.findById(1L))
                .thenReturn(Optional.of(savedEntity));
        when(securitiesDealRepository.save(any(SecuritiesDeal.class)))
                .thenReturn(savedEntity);

        SecuritiesDealResponseDTO result =
                securitiesDealService.updateDeal(1L, validRequest);

        assertThat(result).isNotNull();
        verify(securitiesDealRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should throw when updating CONFIRMED securities deal")
    void shouldThrowWhenUpdatingConfirmedDeal() {
        when(securitiesDealRepository.findById(2L))
                .thenReturn(Optional.of(confirmedEntity));

        assertThatThrownBy(() -> securitiesDealService.updateDeal(2L, validRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Only deals in NEW status can be updated");
    }

    @Test
    @DisplayName("Should delete securities deal when status is NEW")
    void shouldDeleteSuccessfully() {
        when(securitiesDealRepository.findById(1L))
                .thenReturn(Optional.of(savedEntity));
        doNothing().when(securitiesDealRepository).deleteById(1L);

        assertThatNoException()
                .isThrownBy(() -> securitiesDealService.deleteDeal(1L));
        verify(securitiesDealRepository, times(1)).deleteById(1L);
    }

    // ── Status Transition Tests ───────────────────────────────────────────────

    @Test
    @DisplayName("Should transition securities deal from NEW to CONFIRMED")
    void shouldTransitionNewToConfirmed() {
        SecuritiesDeal confirmedResult = SecuritiesDeal.builder()
                .secDealId(1L).cpId(1L).instrumentId(10L)
                .side(SecuritiesSide.BUY)
                .quantity(BigDecimal.TEN).price(BigDecimal.TEN)
                .tradeDate(LocalDate.now())
                .settlementDate(LocalDate.now().plusDays(2))
                .status(DealStatus.CONFIRMED)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        when(securitiesDealRepository.findById(1L))
                .thenReturn(Optional.of(savedEntity));
        when(securitiesDealRepository.save(any(SecuritiesDeal.class)))
                .thenReturn(confirmedResult);

        SecuritiesDealResponseDTO result =
                securitiesDealService.updateStatus(1L, DealStatus.CONFIRMED);

        assertThat(result.getStatus()).isEqualTo(DealStatus.CONFIRMED);
    }

    @Test
    @DisplayName("Should throw on invalid transition NEW to SETTLED directly")
    void shouldThrowOnInvalidTransition() {
        when(securitiesDealRepository.findById(1L))
                .thenReturn(Optional.of(savedEntity));

        assertThatThrownBy(() ->
                securitiesDealService.updateStatus(1L, DealStatus.SETTLED))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid status transition");
    }

    // ── Filter Tests ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should return securities deals by instrument ID")
    void shouldReturnDealsByInstrument() {
        when(securitiesDealRepository.findByInstrumentId(10L))
                .thenReturn(List.of(savedEntity));

        var result = securitiesDealService.getDealsByInstrument(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getInstrumentId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Should return total settled quantity for instrument")
    void shouldReturnTotalSettledQuantity() {
        when(securitiesDealRepository.sumSettledQuantityByInstrument(10L))
                .thenReturn(new BigDecimal("5000.0000"));

        BigDecimal total =
                securitiesDealService.getTotalSettledQuantityByInstrument(10L);

        assertThat(total).isEqualByComparingTo("5000.0000");
    }

    @Test
    @DisplayName("Should return repo deals by counterparty")
    void shouldReturnRepoDealsByCounterparty() {
        when(securitiesDealRepository.findRepoDealsByCounterparty(2L))
                .thenReturn(List.of(confirmedEntity));

        var result = securitiesDealService.getRepoDealsByCounterparty(2L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSide()).isEqualTo(SecuritiesSide.REPO);
    }
}