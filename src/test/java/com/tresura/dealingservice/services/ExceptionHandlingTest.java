package com.tresura.dealingservice.services;

import com.tresura.dealingservice.entities.MMDeal;
import com.tresura.dealingservice.enums.BuySell;
import com.tresura.dealingservice.enums.DealStatus;
import com.tresura.dealingservice.exception.ResourceNotFoundException;
import com.tresura.dealingservice.repository.MMDealRepository;
import com.tresura.dealingservice.repository.FXDealRepository;
import com.tresura.dealingservice.repository.SecuritiesDealRepository;
import com.tresura.dealingservice.service.MMDealServiceImpl;
import com.tresura.dealingservice.service.FXDealServiceImpl;
import com.tresura.dealingservice.service.SecuritiesDealServiceImpl;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Exception Handling Tests - All Deal Types")
class ExceptionHandlingTest {

    @Mock private MMDealRepository mmDealRepository;
    @Mock private FXDealRepository fxDealRepository;
    @Mock private SecuritiesDealRepository securitiesDealRepository;

    @InjectMocks private MMDealServiceImpl mmDealService;
    @InjectMocks private FXDealServiceImpl fxDealService;
    @InjectMocks private SecuritiesDealServiceImpl securitiesDealService;

    // ── ResourceNotFoundException message format tests ────────────────────────

    @Test
    @DisplayName("ResourceNotFoundException should contain resource name")
    void resourceNotFoundShouldContainResourceName() {
        when(mmDealRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mmDealService.getDealById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("MMDeal");
    }

    @Test
    @DisplayName("ResourceNotFoundException should contain field name")
    void resourceNotFoundShouldContainFieldName() {
        when(mmDealRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mmDealService.getDealById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("id");
    }

    @Test
    @DisplayName("ResourceNotFoundException should contain field value")
    void resourceNotFoundShouldContainFieldValue() {
        when(mmDealRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mmDealService.getDealById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("FXDeal ResourceNotFoundException should contain FXDeal")
    void fxDealNotFoundShouldContainFXDeal() {
        when(fxDealRepository.findById(55L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fxDealService.getDealById(55L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("FXDeal");
    }

    @Test
    @DisplayName("SecuritiesDeal ResourceNotFoundException should contain SecuritiesDeal")
    void securitiesDealNotFoundShouldContainSecuritiesDeal() {
        when(securitiesDealRepository.findById(77L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> securitiesDealService.getDealById(77L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("SecuritiesDeal");
    }

    // ── Status transition edge case tests ─────────────────────────────────────

    @Test
    @DisplayName("Should throw when transitioning from CANCELLED to any status")
    void shouldThrowWhenTransitioningFromCancelled() {
        MMDeal cancelledDeal = MMDeal.builder()
                .mmDealId(1L).cpId(1L).buySell(BuySell.BORROW)
                .principal(BigDecimal.TEN).currency("USD").rate(BigDecimal.ONE)
                .tradeDate(LocalDate.now()).startDate(LocalDate.now())
                .maturityDate(LocalDate.now().plusMonths(1))
                .status(DealStatus.CANCELLED)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        when(mmDealRepository.findById(1L)).thenReturn(Optional.of(cancelledDeal));

        assertThatThrownBy(() ->
                mmDealService.updateStatus(1L, DealStatus.CONFIRMED))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid status transition");
    }

    @Test
    @DisplayName("Should throw when transitioning from CONFIRMED to NEW")
    void shouldThrowWhenTransitioningConfirmedToNew() {
        MMDeal confirmedDeal = MMDeal.builder()
                .mmDealId(2L).cpId(1L).buySell(BuySell.LEND)
                .principal(BigDecimal.TEN).currency("INR").rate(BigDecimal.ONE)
                .tradeDate(LocalDate.now()).startDate(LocalDate.now())
                .maturityDate(LocalDate.now().plusMonths(1))
                .status(DealStatus.CONFIRMED)
                .createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now())
                .build();

        when(mmDealRepository.findById(2L)).thenReturn(Optional.of(confirmedDeal));

        assertThatThrownBy(() ->
                mmDealService.updateStatus(2L, DealStatus.NEW))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid status transition");
    }
}
