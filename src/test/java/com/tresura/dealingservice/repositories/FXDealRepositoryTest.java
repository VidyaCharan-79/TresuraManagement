package com.tresura.dealingservice.repositories;

import com.tresura.dealingservice.entities.FXDeal;
import com.tresura.dealingservice.enums.DealStatus;
import com.tresura.dealingservice.enums.FXDealType;
import com.tresura.dealingservice.repository.FXDealRepository;
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
@DisplayName("FXDeal Repository Tests")
class FXDealRepositoryTest {

    @Autowired
    private FXDealRepository fxDealRepository;

    private FXDeal usdInrSpot;
    private FXDeal usdInrForward;
    private FXDeal eurUsdSwap;

    @BeforeEach
    void setUp() {
        fxDealRepository.deleteAll();

        usdInrSpot = fxDealRepository.save(FXDeal.builder()
                .cpId(1L)
                .dealType(FXDealType.SPOT)
                .buyCurrency("USD")
                .sellCurrency("INR")
                .buyAmount(new BigDecimal("100000.00"))
                .sellAmount(new BigDecimal("8300000.00"))
                .rate(new BigDecimal("83.00"))
                .tradeDate(LocalDate.of(2025, 1, 10))
                .valueDate(LocalDate.of(2025, 1, 12))
                .status(DealStatus.NEW)
                .build());

        usdInrForward = fxDealRepository.save(FXDeal.builder()
                .cpId(1L)
                .dealType(FXDealType.FORWARD)
                .buyCurrency("USD")
                .sellCurrency("INR")
                .buyAmount(new BigDecimal("200000.00"))
                .sellAmount(new BigDecimal("16800000.00"))
                .rate(new BigDecimal("84.00"))
                .tradeDate(LocalDate.of(2025, 1, 15))
                .valueDate(LocalDate.of(2025, 4, 15))
                .status(DealStatus.CONFIRMED)
                .build());

        eurUsdSwap = fxDealRepository.save(FXDeal.builder()
                .cpId(2L)
                .dealType(FXDealType.SWAP)
                .buyCurrency("EUR")
                .sellCurrency("USD")
                .buyAmount(new BigDecimal("50000.00"))
                .sellAmount(new BigDecimal("54000.00"))
                .rate(new BigDecimal("1.08"))
                .tradeDate(LocalDate.of(2025, 2, 1))
                .valueDate(LocalDate.of(2025, 2, 3))
                .status(DealStatus.NEW)
                .build());
    }


    @Test
    @DisplayName("Should save and retrieve FX deal by ID")
    void shouldSaveAndRetrieveById() {
        Optional<FXDeal> found =
                fxDealRepository.findById(usdInrSpot.getFxDealId());

        assertThat(found).isPresent();
        assertThat(found.get().getDealType()).isEqualTo(FXDealType.SPOT);
        assertThat(found.get().getBuyCurrency()).isEqualTo("USD");
        assertThat(found.get().getSellCurrency()).isEqualTo("INR");
    }

    @Test
    @DisplayName("Should return all 3 FX deals")
    void shouldReturnAllDeals() {
        List<FXDeal> all = fxDealRepository.findAll();
        assertThat(all).hasSize(3);
    }

    @Test
    @DisplayName("Should delete FX deal by ID")
    void shouldDeleteById() {
        fxDealRepository.deleteById(usdInrSpot.getFxDealId());

        Optional<FXDeal> found =
                fxDealRepository.findById(usdInrSpot.getFxDealId());
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Should update FX deal status")
    void shouldUpdateStatus() {
        usdInrSpot.setStatus(DealStatus.CONFIRMED);
        fxDealRepository.save(usdInrSpot);

        Optional<FXDeal> updated =
                fxDealRepository.findById(usdInrSpot.getFxDealId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getStatus()).isEqualTo(DealStatus.CONFIRMED);
    }

    // ── Filter by Single Field ────────────────────────────────────────────────

    @Test
    @DisplayName("Should find FX deals by counterparty ID")
    void shouldFindByCpId() {
        List<FXDeal> result = fxDealRepository.findByCpId(1L);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(d -> d.getCpId().equals(1L));
    }

    @Test
    @DisplayName("Should find FX deals by deal type SPOT")
    void shouldFindByDealTypeSpot() {
        List<FXDeal> result = fxDealRepository.findByDealType(FXDealType.SPOT);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDealType()).isEqualTo(FXDealType.SPOT);
    }

    @Test
    @DisplayName("Should find FX deals by deal type SWAP")
    void shouldFindByDealTypeSwap() {
        List<FXDeal> result = fxDealRepository.findByDealType(FXDealType.SWAP);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBuyCurrency()).isEqualTo("EUR");
    }

    @Test
    @DisplayName("Should find FX deals by status NEW")
    void shouldFindByStatusNew() {
        List<FXDeal> result = fxDealRepository.findByStatus(DealStatus.NEW);

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(d -> d.getStatus() == DealStatus.NEW);
    }

    @Test
    @DisplayName("Should find FX deals by buy currency USD")
    void shouldFindByBuyCurrency() {
        List<FXDeal> result = fxDealRepository.findByBuyCurrency("USD");

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(d -> d.getBuyCurrency().equals("USD"));
    }

    @Test
    @DisplayName("Should find FX deals by currency pair USD/INR")
    void shouldFindByCurrencyPair() {
        List<FXDeal> result =
                fxDealRepository.findByBuyCurrencyAndSellCurrency("USD", "INR");

        assertThat(result).hasSize(2);
        assertThat(result).allMatch(d ->
                d.getBuyCurrency().equals("USD") &&
                        d.getSellCurrency().equals("INR"));
    }

    @Test
    @DisplayName("Should return empty for non-existent currency pair")
    void shouldReturnEmptyForNonExistentPair() {
        List<FXDeal> result =
                fxDealRepository.findByBuyCurrencyAndSellCurrency("GBP", "JPY");

        assertThat(result).isEmpty();
    }

    // ── Filter by Date Range ──────────────────────────────────────────────────

    @Test
    @DisplayName("Should find FX deals by trade date range")
    void shouldFindByTradeDateRange() {
        List<FXDeal> result = fxDealRepository.findByTradeDateBetween(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 31));

        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Should find FX deals by value date range")
    void shouldFindByValueDateRange() {
        List<FXDeal> result = fxDealRepository.findByValueDateBetween(
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 2, 28));

        // SPOT (Jan 12), SWAP (Feb 3) qualify — FORWARD (Apr 15) does not
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Should find FX deals settling on or before a date")
    void shouldFindByValueDateLessThanEqual() {
        List<FXDeal> result = fxDealRepository.findByValueDateLessThanEqual(
                LocalDate.of(2025, 2, 3));

        // SPOT (Jan 12) and SWAP (Feb 3) qualify
        assertThat(result).hasSize(2);
    }

    // ── Custom JPQL Queries ───────────────────────────────────────────────────

    @Test
    @DisplayName("Should find active deals by currency pair using JPQL")
    void shouldFindActiveDealsByCurrencyPair() {
        List<FXDeal> result = fxDealRepository.findActiveDealsByCurrencyPair(
                "USD", "INR", DealStatus.NEW);

        // Only SPOT is NEW, FORWARD is CONFIRMED
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDealType()).isEqualTo(FXDealType.SPOT);
    }

    @Test
    @DisplayName("Should find deals by counterparty and value date range using JPQL")
    void shouldFindByCounterpartyAndValueDateRange() {
        List<FXDeal> result = fxDealRepository.findByCounterpartyAndValueDateRange(
                1L,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 4, 30));

        // Both CP 1 deals qualify (SPOT Jan12, FORWARD Apr15)
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Should return empty when no deals match CP and value date range")
    void shouldReturnEmptyForNonMatchingCpAndDateRange() {
        List<FXDeal> result = fxDealRepository.findByCounterpartyAndValueDateRange(
                2L,
                LocalDate.of(2025, 5, 1),
                LocalDate.of(2025, 12, 31));

        // CP 2 SWAP settles Feb 3 — outside range
        assertThat(result).isEmpty();
    }
}