package com.tresura.dealingservice.repositories;

import com.tresura.dealingservice.entities.MMDeal;
import com.tresura.dealingservice.enums.BuySell;
import com.tresura.dealingservice.enums.DealStatus;
import com.tresura.dealingservice.repository.MMDealRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("MMDeal Repository Tests")
class MMDealRepositoryTest {

    @Autowired
    private MMDealRepository mmDealRepository;

    private MMDeal usdBorrowDeal;
    private MMDeal inrLendDeal;
    private MMDeal eurBorrowDeal;

    @BeforeEach
    void setUp() {
        // Clean slate before each test
        mmDealRepository.deleteAll();

        usdBorrowDeal = mmDealRepository.save(MMDeal.builder()
                .cpId(1L)
                .buySell(BuySell.BORROW)
                .principal(new BigDecimal("1000000.00"))
                .currency("USD")
                .rate(new BigDecimal("5.25"))
                .tradeDate(LocalDate.of(2025, 1, 10))
                .startDate(LocalDate.of(2025, 1, 11))
                .maturityDate(LocalDate.of(2025, 6, 30))
                .status(DealStatus.NEW)
                .build());

        inrLendDeal = mmDealRepository.save(MMDeal.builder()
                .cpId(2L)
                .buySell(BuySell.LEND)
                .principal(new BigDecimal("5000000.00"))
                .currency("INR")
                .rate(new BigDecimal("7.50"))
                .tradeDate(LocalDate.of(2025, 1, 15))
                .startDate(LocalDate.of(2025, 1, 16))
                .maturityDate(LocalDate.of(2025, 3, 31))
                .status(DealStatus.CONFIRMED)
                .build());

        eurBorrowDeal = mmDealRepository.save(MMDeal.builder()
                .cpId(1L)
                .buySell(BuySell.BORROW)
                .principal(new BigDecimal("2000000.00"))
                .currency("EUR")
                .rate(new BigDecimal("4.00"))
                .tradeDate(LocalDate.of(2025, 2, 1))
                .startDate(LocalDate.of(2025, 2, 2))
                .maturityDate(LocalDate.of(2025, 12, 31))
                .status(DealStatus.NEW)
                .build());
    }

    // ── Basic CRUD ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should save and retrieve MM deal by ID")
    void shouldSaveAndRetrieveById() {
        Optional<MMDeal> found =
                mmDealRepository.findById(usdBorrowDeal.getMmDealId());

        assertThat(found).isPresent();
        assertThat(found.get().getCurrency()).isEqualTo("USD");
        assertThat(found.get().getBuySell()).isEqualTo(BuySell.BORROW);
        assertThat(found.get().getPrincipal())
                .isEqualByComparingTo("1000000.00");
    }

    @Test
    @DisplayName("Should return empty when ID does not exist")
    void shouldReturnEmptyForNonExistentId() {
        Optional<MMDeal> found = mmDealRepository.findById(9999L);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should save MM deal with NEW status by default")
    void shouldSaveWithNewStatus() {
        Optional<MMDeal> found =
                mmDealRepository.findById(usdBorrowDeal.getMmDealId());

        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo(DealStatus.NEW);
    }

    @Test
    @DisplayName("Should return all 3 saved MM deals")
    void shouldReturnAllDeals() {
        List<MMDeal> all = mmDealRepository.findAll();
        assertThat(all).hasSize(3);
    }

    @Test
    @DisplayName("Should delete MM deal by ID")
    void shouldDeleteById() {
        mmDealRepository.deleteById(usdBorrowDeal.getMmDealId());

        Optional<MMDeal> found =
                mmDealRepository.findById(usdBorrowDeal.getMmDealId());
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should update MM deal status")
    void shouldUpdateDealStatus() {
        usdBorrowDeal.setStatus(DealStatus.CONFIRMED);
        mmDealRepository.save(usdBorrowDeal);

        Optional<MMDeal> updated =
                mmDealRepository.findById(usdBorrowDeal.getMmDealId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getStatus()).isEqualTo(DealStatus.CONFIRMED);
    }

    // ── Filter by Single Field ────────────────────────────────────────────────

    @Test
    @DisplayName("Should find MM deals by counterparty ID")
    void shouldFindByCpId() {
        List<MMDeal> result = mmDealRepository.findByCpId(1L);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(d -> d.getCpId().equals(1L));
    }

    @Test
    @DisplayName("Should return empty list for non-existent counterparty")
    void shouldReturnEmptyForNonExistentCpId() {
        List<MMDeal> result = mmDealRepository.findByCpId(999L);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should find MM deals by status NEW")
    void shouldFindByStatusNew() {
        List<MMDeal> result = mmDealRepository.findByStatus(DealStatus.NEW);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(d -> d.getStatus() == DealStatus.NEW);
    }

    @Test
    @DisplayName("Should find MM deals by status CONFIRMED")
    void shouldFindByStatusConfirmed() {
        List<MMDeal> result = mmDealRepository.findByStatus(DealStatus.CONFIRMED);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCurrency()).isEqualTo("INR");
    }

    @Test
    @DisplayName("Should find MM deals by currency USD")
    void shouldFindByCurrency() {
        List<MMDeal> result = mmDealRepository.findByCurrency("USD");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCurrency()).isEqualTo("USD");
    }

    @Test
    @DisplayName("Should find MM deals by BuySell BORROW")
    void shouldFindByBuySellBorrow() {
        List<MMDeal> result = mmDealRepository.findByBuySell(BuySell.BORROW);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(d -> d.getBuySell() == BuySell.BORROW);
    }

    @Test
    @DisplayName("Should find MM deals by BuySell LEND")
    void shouldFindByBuySellLend() {
        List<MMDeal> result = mmDealRepository.findByBuySell(BuySell.LEND);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCurrency()).isEqualTo("INR");
    }

    // ── Filter by Combination ─────────────────────────────────────────────────

    @Test
    @DisplayName("Should find MM deals by counterparty and status")
    void shouldFindByCpIdAndStatus() {
        List<MMDeal> result =
                mmDealRepository.findByCpIdAndStatus(1L, DealStatus.NEW);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(d ->
                d.getCpId().equals(1L) && d.getStatus() == DealStatus.NEW);
    }

    @Test
    @DisplayName("Should return empty when no deals match CP and status combo")
    void shouldReturnEmptyForNonMatchingCpAndStatus() {
        List<MMDeal> result =
                mmDealRepository.findByCpIdAndStatus(2L, DealStatus.NEW);

        assertThat(result).isEmpty();
    }

    // ── Filter by Date Range ──────────────────────────────────────────────────

    @Test
    @DisplayName("Should find MM deals by trade date range")
    void shouldFindByTradeDateRange() {
        List<MMDeal> result = mmDealRepository.findByTradeDateBetween(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 31));

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Should find single deal in narrow trade date range")
    void shouldFindSingleDealInNarrowRange() {
        List<MMDeal> result = mmDealRepository.findByTradeDateBetween(
                LocalDate.of(2025, 2, 1),
                LocalDate.of(2025, 2, 28));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCurrency()).isEqualTo("EUR");
    }

    @Test
    @DisplayName("Should find MM deals maturing on or before a date")
    void shouldFindByMaturityDateLessThanEqual() {
        List<MMDeal> result = mmDealRepository.findByMaturityDateLessThanEqual(
                LocalDate.of(2025, 6, 30));

        // USD matures June 30, INR matures March 31 — both qualify
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Should return only INR deal for early maturity date")
    void shouldReturnEarlyMaturingDeal() {
        List<MMDeal> result = mmDealRepository.findByMaturityDateLessThanEqual(
                LocalDate.of(2025, 3, 31));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCurrency()).isEqualTo("INR");
    }

    // ── Custom JPQL Queries ───────────────────────────────────────────────────

    @Test
    @DisplayName("Should find maturing deals by status using JPQL")
    void shouldFindMaturingDealsByStatus() {
        List<MMDeal> result = mmDealRepository.findMaturingDealsByStatus(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 6, 30),
                DealStatus.NEW);

        // USD deal is NEW and matures June 30 — qualifies
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCurrency()).isEqualTo("USD");
    }

    @Test
    @DisplayName("Should find deals by currency and principal range using JPQL")
    void shouldFindByCurrencyAndPrincipalRange() {
        // Insert a second USD deal with different principal
        mmDealRepository.save(MMDeal.builder()
                .cpId(3L)
                .buySell(BuySell.LEND)
                .principal(new BigDecimal("3000000.00"))
                .currency("USD")
                .rate(new BigDecimal("5.00"))
                .tradeDate(LocalDate.of(2025, 3, 1))
                .startDate(LocalDate.of(2025, 3, 2))
                .maturityDate(LocalDate.of(2025, 9, 30))
                .status(DealStatus.NEW)
                .build());

        List<MMDeal> result = mmDealRepository.findByCurrencyAndPrincipalRange(
                "USD",
                new BigDecimal("500000"),
                new BigDecimal("2000000"));

        // Only the 1,000,000 USD deal qualifies (3,000,000 is out of range)
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPrincipal())
                .isEqualByComparingTo("1000000.00");
    }

    @Test
    @DisplayName("Should return empty when no deals in principal range")
    void shouldReturnEmptyForOutOfRangePrincipal() {
        List<MMDeal> result = mmDealRepository.findByCurrencyAndPrincipalRange(
                "USD",
                new BigDecimal("10000000"),
                new BigDecimal("99000000"));

        assertThat(result).isEmpty();
    }
}