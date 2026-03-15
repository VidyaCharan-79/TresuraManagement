package com.tresura.dealingservice.repository;

import com.tresura.dealingservice.entities.SecuritiesDeal;
import com.tresura.dealingservice.enums.DealStatus;
import com.tresura.dealingservice.enums.SecuritiesSide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SecuritiesDealRepository extends JpaRepository<SecuritiesDeal, Long> {

    // ── Find by single field ─────────────────────────────────────────────────

    List<SecuritiesDeal> findByCpId(Long cpId);

    List<SecuritiesDeal> findByStatus(DealStatus status);

    List<SecuritiesDeal> findBySide(SecuritiesSide side);

    List<SecuritiesDeal> findByInstrumentId(Long instrumentId);

    // ── Find by combination ──────────────────────────────────────────────────

    List<SecuritiesDeal> findByCpIdAndStatus(Long cpId, DealStatus status);

    List<SecuritiesDeal> findBySideAndStatus(SecuritiesSide side, DealStatus status);

    List<SecuritiesDeal> findByInstrumentIdAndStatus(Long instrumentId, DealStatus status);

    List<SecuritiesDeal> findByCpIdAndInstrumentId(Long cpId, Long instrumentId);

    // ── Find by date ranges ──────────────────────────────────────────────────

    List<SecuritiesDeal> findByTradeDateBetween(LocalDate fromDate, LocalDate toDate);

    List<SecuritiesDeal> findBySettlementDateBetween(LocalDate fromDate, LocalDate toDate);

    // ── Find by settlement date on or before ─────────────────────────────────
    //    Useful for: "show me all deals due for settlement today"

    List<SecuritiesDeal> findBySettlementDateLessThanEqual(LocalDate date);

    // ── Find by price range ───────────────────────────────────────────────────
    //    Useful for: "show me all bonds priced between 95 and 105"

    List<SecuritiesDeal> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    // ── Custom JPQL: deals for an instrument within a settlement date range ───
    @Query("SELECT s FROM SecuritiesDeal s " +
            "WHERE s.instrumentId = :instrumentId " +
            "AND s.settlementDate BETWEEN :fromDate AND :toDate")
    List<SecuritiesDeal> findByInstrumentAndSettlementDateRange(
            @Param("instrumentId") Long instrumentId,
            @Param("fromDate")     LocalDate fromDate,
            @Param("toDate")       LocalDate toDate);

    // ── Custom JPQL: all REPO deals for a counterparty ───────────────────────
    @Query("SELECT s FROM SecuritiesDeal s " +
            "WHERE s.cpId = :cpId " +
            "AND s.side IN (com.tresura.dealingservice.enums.SecuritiesSide.REPO, " +
            "               com.tresura.dealingservice.enums.SecuritiesSide.REVERSE_REPO)")
    List<SecuritiesDeal> findRepoDealsByCounterparty(@Param("cpId") Long cpId);

    // ── Custom JPQL: total quantity held for an instrument ───────────────────
    @Query("SELECT COALESCE(SUM(s.quantity), 0) FROM SecuritiesDeal s " +
            "WHERE s.instrumentId = :instrumentId " +
            "AND s.status = com.tresura.dealingservice.enums.DealStatus.SETTLED")
    BigDecimal sumSettledQuantityByInstrument(@Param("instrumentId") Long instrumentId);
}
