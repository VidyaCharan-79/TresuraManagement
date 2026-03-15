package com.tresura.dealingservice.repositories;

import com.tresura.dealingservice.entities.SecuritiesDeal;
import com.tresura.dealingservice.enums.DealStatus;
import com.tresura.dealingservice.enums.SecuritiesSide;
import com.tresura.dealingservice.repository.SecuritiesDealRepository;
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
@DisplayName("SecuritiesDeal Repository Tests")
class SecuritiesDealRepositoryTest {

    @Autowired
    private SecuritiesDealRepository securitiesDealRepository;

    private SecuritiesDeal buyDeal;
    private SecuritiesDeal sellDeal;
    private SecuritiesDeal repoDeal;
    private SecuritiesDeal reverseRepoDeal;

    @BeforeEach
    void setUp() {
        securitiesDealRepository.deleteAll();

        buyDeal = securitiesDealRepository.save(SecuritiesDeal.builder()
                .cpId(1L)
                .instrumentId(10L)
                .side(SecuritiesSide.BUY)
                .quantity(new BigDecimal("1000.0000"))
                .price(new BigDecimal("98.50"))
                .tradeDate(LocalDate.of(2025, 1, 10))
                .settlementDate(LocalDate.of(2025, 1, 13))
                .status(DealStatus.NEW)
                .build());

        sellDeal = securitiesDealRepository.save(SecuritiesDeal.builder()
                .cpId(2L)
                .instrumentId(10L)
                .side(SecuritiesSide.SELL)
                .quantity(new BigDecimal("500.0000"))
                .price(new BigDecimal("99.00"))
                .tradeDate(LocalDate.of(2025, 1, 15))
                .settlementDate(LocalDate.of(2025, 1, 18))
                .status(DealStatus.CONFIRMED)
                .build());

        repoDeal = securitiesDealRepository.save(SecuritiesDeal.builder()
                .cpId(1L)
                .instrumentId(20L)
                .side(SecuritiesSide.REPO)
                .quantity(new BigDecimal("2000.0000"))
                .price(new BigDecimal("100.00"))
                .tradeDate(LocalDate.of(2025, 2, 1))
                .settlementDate(LocalDate.of(2025, 2, 4))
                .status(DealStatus.NEW)
                .build());

        reverseRepoDeal = securitiesDealRepository.save(SecuritiesDeal.builder()
                .cpId(1L)
                .instrumentId(20L)
                .side(SecuritiesSide.REVERSE_REPO)
                .quantity(new BigDecimal("3000.0000"))
                .price(new BigDecimal("100.00"))
                .tradeDate(LocalDate.of(2025, 2, 5))
                .settlementDate(LocalDate.of(2025, 2, 8))
                .status(DealStatus.SETTLED)
                .build());
    }

    // ── Basic CRUD ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Should save and retrieve securities deal by ID")
    void shouldSaveAndRetrieveById() {
        Optional<SecuritiesDeal> found =
                securitiesDealRepository.findById(buyDeal.getSecDealId());

        assertThat(found).isPresent();
        assertThat(found.get().getSide()).isEqualTo(SecuritiesSide.BUY);
        assertThat(found.get().getQuantity())
                .isEqualByComparingTo("1000.0000");
        assertThat(found.get().getPrice())
                .isEqualByComparingTo("98.50");
    }

    @Test
    @DisplayName("Should return all 4 securities deals")
    void shouldReturnAllDeals() {
        List<SecuritiesDeal> all = securitiesDealRepository.findAll();
        assertThat(all).hasSize(4);
    }

    @Test
    @DisplayName("Should delete securities deal by ID")
    void shouldDeleteById() {
        securitiesDealRepository.deleteById(buyDeal.getSecDealId());

        Optional<SecuritiesDeal> found =
                securitiesDealRepository.findById(buyDeal.getSecDealId());
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should update securities deal status")
    void shouldUpdateStatus() {
        buyDeal.setStatus(DealStatus.CONFIRMED);
        securitiesDealRepository.save(buyDeal);

        Optional<SecuritiesDeal> updated =
                securitiesDealRepository.findById(buyDeal.getSecDealId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getStatus()).isEqualTo(DealStatus.CONFIRMED);
    }

    // ── Filter by Single Field ────────────────────────────────────────────────

    @Test
    @DisplayName("Should find securities deals by counterparty ID")
    void shouldFindByCpId() {
        List<SecuritiesDeal> result =
                securitiesDealRepository.findByCpId(1L);

        assertThat(result).hasSize(3);
        assertThat(result).allMatch(d -> d.getCpId().equals(1L));
    }

    @Test
    @DisplayName("Should find securities deals by status NEW")
    void shouldFindByStatusNew() {
        List<SecuritiesDeal> result =
                securitiesDealRepository.findByStatus(DealStatus.NEW);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(d -> d.getStatus() == DealStatus.NEW);
    }

    @Test
    @DisplayName("Should find securities deals by side BUY")
    void shouldFindBySideBuy() {
        List<SecuritiesDeal> result =
                securitiesDealRepository.findBySide(SecuritiesSide.BUY);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSide()).isEqualTo(SecuritiesSide.BUY);
    }

    @Test
    @DisplayName("Should find securities deals by instrument ID")
    void shouldFindByInstrumentId() {
        List<SecuritiesDeal> result =
                securitiesDealRepository.findByInstrumentId(10L);

        // Both BUY and SELL are for instrument 10
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(d -> d.getInstrumentId().equals(10L));
    }

    @Test
    @DisplayName("Should find securities deals by instrument and status")
    void shouldFindByInstrumentAndStatus() {
        List<SecuritiesDeal> result =
                securitiesDealRepository.findByInstrumentIdAndStatus(
                        10L, DealStatus.NEW);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSide()).isEqualTo(SecuritiesSide.BUY);
    }

    // ── Filter by Date Range ──────────────────────────────────────────────────

    @Test
    @DisplayName("Should find securities deals by trade date range")
    void shouldFindByTradeDateRange() {
        List<SecuritiesDeal> result =
                securitiesDealRepository.findByTradeDateBetween(
                        LocalDate.of(2025, 1, 1),
                        LocalDate.of(2025, 1, 31));

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Should find securities deals by settlement date range")
    void shouldFindBySettlementDateRange() {
        List<SecuritiesDeal> result =
                securitiesDealRepository.findBySettlementDateBetween(
                        LocalDate.of(2025, 1, 1),
                        LocalDate.of(2025, 1, 31));

        // BUY (Jan 13) and SELL (Jan 18) qualify
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Should find deals due for settlement on or before a date")
    void shouldFindBySettlementDateLessThanEqual() {
        List<SecuritiesDeal> result =
                securitiesDealRepository.findBySettlementDateLessThanEqual(
                        LocalDate.of(2025, 1, 18));

        // BUY (Jan 13) and SELL (Jan 18) qualify
        assertThat(result).hasSize(2);
    }

    // ── Custom JPQL Queries ───────────────────────────────────────────────────

    @Test
    @DisplayName("Should find REPO and REVERSE_REPO deals by counterparty using JPQL")
    void shouldFindRepoDealsByCounterparty() {
        List<SecuritiesDeal> result =
                securitiesDealRepository.findRepoDealsByCounterparty(1L);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(d ->
                d.getSide() == SecuritiesSide.REPO ||
                        d.getSide() == SecuritiesSide.REVERSE_REPO);
    }

    @Test
    @DisplayName("Should return zero for instrument with no settled deals")
    void shouldReturnZeroForNoSettledDeals() {
        BigDecimal total =
                securitiesDealRepository.sumSettledQuantityByInstrument(10L);

        // Instrument 10 has BUY (NEW) and SELL (CONFIRMED) — none SETTLED
        assertThat(total).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should return correct total settled quantity using JPQL")
    void shouldReturnTotalSettledQuantity() {
        BigDecimal total =
                securitiesDealRepository.sumSettledQuantityByInstrument(20L);

        // REPO is NEW (excluded), REVERSE_REPO is SETTLED (3000)
        assertThat(total).isEqualByComparingTo("3000.0000");
    }

    @Test
    @DisplayName("Should find deals by instrument and settlement date range using JPQL")
    void shouldFindByInstrumentAndSettlementDateRange() {
        List<SecuritiesDeal> result =
                securitiesDealRepository.findByInstrumentAndSettlementDateRange(
                        20L,
                        LocalDate.of(2025, 2, 1),
                        LocalDate.of(2025, 2, 28));

        // REPO (Feb 4) and REVERSE_REPO (Feb 8) both qualify for instrument 20
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Should return empty for instrument with no deals in date range")
    void shouldReturnEmptyForOutOfRangeDates() {
        List<SecuritiesDeal> result =
                securitiesDealRepository.findByInstrumentAndSettlementDateRange(
                        10L,
                        LocalDate.of(2025, 6, 1),
                        LocalDate.of(2025, 12, 31));

        assertThat(result).isEmpty();
    }
}
