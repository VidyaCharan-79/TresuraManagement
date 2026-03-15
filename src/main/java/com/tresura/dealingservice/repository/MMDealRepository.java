package com.tresura.dealingservice.repository;

import com.tresura.dealingservice.entities.MMDeal;
import com.tresura.dealingservice.enums.BuySell;
import com.tresura.dealingservice.enums.DealStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MMDealRepository extends JpaRepository<MMDeal, Long> {

    // ── Find by single field ─────────────────────────────────────────────────

    List<MMDeal> findByCpId(Long cpId);

    List<MMDeal> findByStatus(DealStatus status);

    List<MMDeal> findByCurrency(String currency);

    List<MMDeal> findByBuySell(BuySell buySell);

    // ── Find by combination ──────────────────────────────────────────────────

    List<MMDeal> findByCpIdAndStatus(Long cpId, DealStatus status);

    List<MMDeal> findByCurrencyAndStatus(String currency, DealStatus status);

    // ── Find by date ranges ──────────────────────────────────────────────────

    List<MMDeal> findByTradeDateBetween(LocalDate fromDate, LocalDate toDate);

    List<MMDeal> findByMaturityDateBetween(LocalDate fromDate, LocalDate toDate);

    // ── Find deals maturing on or before a date ──────────────────────────────
    //    Useful for: "show me all deals maturing this week"

    List<MMDeal> findByMaturityDateLessThanEqual(LocalDate date);

    // ── Find deals above a principal threshold ───────────────────────────────
    //    Useful for: "show me all deals above 1 crore"

    List<MMDeal> findByPrincipalGreaterThanEqual(BigDecimal minPrincipal);

    // ── Custom JPQL: deals maturing between today and a future date ──────────
    @Query("SELECT m FROM MMDeal m " +
            "WHERE m.maturityDate BETWEEN :today AND :futureDate " +
            "AND m.status = :status")
    List<MMDeal> findMaturingDealsByStatus(
            @Param("today")      LocalDate today,
            @Param("futureDate") LocalDate futureDate,
            @Param("status")     DealStatus status);

    // ── Custom JPQL: search by currency and principal range ──────────────────
    @Query("SELECT m FROM MMDeal m " +
            "WHERE m.currency = :currency " +
            "AND m.principal BETWEEN :minAmount AND :maxAmount")
    List<MMDeal> findByCurrencyAndPrincipalRange(
            @Param("currency")  String currency,
            @Param("minAmount") BigDecimal minAmount,
            @Param("maxAmount") BigDecimal maxAmount);
}