package com.tresura.dealingservice.service;

import com.tresura.dealingservice.dto.FXDealRequestDTO;
import com.tresura.dealingservice.dto.FXDealResponseDTO;
import com.tresura.dealingservice.enums.DealStatus;
import com.tresura.dealingservice.enums.FXDealType;

import java.time.LocalDate;
import java.util.List;

public interface FXDealService {

    // ── CRUD ─────────────────────────────────────────
    FXDealResponseDTO createDeal(FXDealRequestDTO requestDTO);
    FXDealResponseDTO getDealById(Long id);
    List<FXDealResponseDTO> getAllDeals();
    FXDealResponseDTO updateDeal(Long id, FXDealRequestDTO requestDTO);
    void deleteDeal(Long id);

    // ── Status Management ────────────────────────────
    FXDealResponseDTO updateStatus(Long id, DealStatus newStatus);

    // ── Filters ──────────────────────────────────────
    List<FXDealResponseDTO> getDealsByCounterparty(Long cpId);
    List<FXDealResponseDTO> getDealsByStatus(DealStatus status);
    List<FXDealResponseDTO> getDealsByDealType(FXDealType dealType);
    List<FXDealResponseDTO> getDealsByCurrencyPair(String buyCurrency, String sellCurrency);
    List<FXDealResponseDTO> getDealsByTradeDateRange(LocalDate fromDate, LocalDate toDate);
    List<FXDealResponseDTO> getDealsByValueDateRange(LocalDate fromDate, LocalDate toDate);
    List<FXDealResponseDTO> getDealsSettlingOn(LocalDate date);
    List<FXDealResponseDTO> getDealsByCounterpartyAndStatus(Long cpId, DealStatus status);
    List<FXDealResponseDTO> getActiveDealsByCurrencyPair(String buyCurrency,
                                                         String sellCurrency,
                                                         DealStatus status);
}