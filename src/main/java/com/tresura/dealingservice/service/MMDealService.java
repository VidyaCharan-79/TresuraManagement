package com.tresura.dealingservice.service;

import com.tresura.dealingservice.dto.MMDealRequestDTO;
import com.tresura.dealingservice.dto.MMDealResponseDTO;
import com.tresura.dealingservice.enums.BuySell;
import com.tresura.dealingservice.enums.DealStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface MMDealService {

    // ── CRUD ─────────────────────────────────────────
    MMDealResponseDTO createDeal(MMDealRequestDTO requestDTO);
    MMDealResponseDTO getDealById(Long id);
    List<MMDealResponseDTO> getAllDeals();
    MMDealResponseDTO updateDeal(Long id, MMDealRequestDTO requestDTO);
    void deleteDeal(Long id);

    // ── Status Management ────────────────────────────
    MMDealResponseDTO updateStatus(Long id, DealStatus newStatus);

    // ── Filters ──────────────────────────────────────
    List<MMDealResponseDTO> getDealsByCounterparty(Long cpId);
    List<MMDealResponseDTO> getDealsByStatus(DealStatus status);
    List<MMDealResponseDTO> getDealsByCurrency(String currency);
    List<MMDealResponseDTO> getDealsByBuySell(BuySell buySell);
    List<MMDealResponseDTO> getDealsByTradeDateRange(LocalDate fromDate, LocalDate toDate);
    List<MMDealResponseDTO> getDealsMaturingOn(LocalDate date);
    List<MMDealResponseDTO> getDealsByCounterpartyAndStatus(Long cpId, DealStatus status);
    List<MMDealResponseDTO> getMaturingDealsByStatus(LocalDate today,
                                                     LocalDate futureDate,
                                                     DealStatus status);
    List<MMDealResponseDTO> getDealsByCurrencyAndPrincipalRange(String currency,
                                                                BigDecimal minAmount,
                                                                BigDecimal maxAmount);
}