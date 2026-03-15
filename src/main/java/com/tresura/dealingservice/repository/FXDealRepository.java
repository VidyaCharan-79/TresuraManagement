package com.tresura.dealingservice.repository;

import com.tresura.dealingservice.entities.FXDeal;
import com.tresura.dealingservice.enums.DealStatus;
import com.tresura.dealingservice.enums.FXDealType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FXDealRepository extends JpaRepository<FXDeal, Long> {

    // ── Find by single field ─────────────────────────────────────────────────

    List<FXDeal> findByCpId(Long cpId);

    List<FXDeal> findByStatus(DealStatus status);

    List<FXDeal> findByDealType(FXDealType dealType);

    List<FXDeal> findByBuyCurrency(String buyCurrency);

    List<FXDeal> findBySellCurrency(String sellCurrency);

    // ── Find by combination ──────────────────────────────────────────────────

    List<FXDeal> findByCpIdAndStatus(Long cpId, DealStatus status);

    List<FXDeal> findByDealTypeAndStatus(FXDealType dealType, DealStatus status);

    List<FXDeal> findByCpIdAndDealType(Long cpId, FXDealType dealType);

    // ── Find by currency pair ─────────────────────────────────────────────────
    //    Useful for: "show all USD/INR deals"

    List<FXDeal> findByBuyCurrencyAndSellCurrency(String buyCurrency, String sellCurrency);

    // ── Find by date ranges ──────────────────────────────────────────────────

    List<FXDeal> findByTradeDateBetween(LocalDate fromDate, LocalDate toDate);

    List<FXDeal> findByValueDateBetween(LocalDate fromDate, LocalDate toDate);

    // ── Find by value date on or before a date ───────────────────────────────
    //    Useful for: "show me all deals settling today"

    List<FXDeal> findByValueDateLessThanEqual(LocalDate date);

    // ── Custom JPQL: all active deals for a currency pair ────────────────────
    @Query("SELECT f FROM FXDeal f " +
            "WHERE f.buyCurrency = :buyCurrency " +
            "AND f.sellCurrency = :sellCurrency " +
            "AND f.status = :status")
    List<FXDeal> findActiveDealsByCurrencyPair(
            @Param("buyCurrency")  String buyCurrency,
            @Param("sellCurrency") String sellCurrency,
            @Param("status")       DealStatus status);

    // ── Custom JPQL: deals by counterparty within a value date range ─────────
    @Query("SELECT f FROM FXDeal f " +
            "WHERE f.cpId = :cpId " +
            "AND f.valueDate BETWEEN :fromDate AND :toDate")
    List<FXDeal> findByCounterpartyAndValueDateRange(
            @Param("cpId")     Long cpId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate")   LocalDate toDate);
}