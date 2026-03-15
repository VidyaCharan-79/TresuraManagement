package com.tresura.dealingservice.service;

import com.tresura.dealingservice.dto.SecuritiesDealRequestDTO;
import com.tresura.dealingservice.dto.SecuritiesDealResponseDTO;
import com.tresura.dealingservice.enums.DealStatus;
import com.tresura.dealingservice.enums.SecuritiesSide;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface SecuritiesDealService {

    // ── CRUD ─────────────────────────────────────────
    SecuritiesDealResponseDTO createDeal(SecuritiesDealRequestDTO requestDTO);
    SecuritiesDealResponseDTO getDealById(Long id);
    List<SecuritiesDealResponseDTO> getAllDeals();
    SecuritiesDealResponseDTO updateDeal(Long id, SecuritiesDealRequestDTO requestDTO);
    void deleteDeal(Long id);

    // ── Status Management ────────────────────────────
    SecuritiesDealResponseDTO updateStatus(Long id, DealStatus newStatus);

    // ── Filters ──────────────────────────────────────
    List<SecuritiesDealResponseDTO> getDealsByCounterparty(Long cpId);
    List<SecuritiesDealResponseDTO> getDealsByStatus(DealStatus status);
    List<SecuritiesDealResponseDTO> getDealsBySide(SecuritiesSide side);
    List<SecuritiesDealResponseDTO> getDealsByInstrument(Long instrumentId);
    List<SecuritiesDealResponseDTO> getDealsByTradeDateRange(LocalDate fromDate, LocalDate toDate);
    List<SecuritiesDealResponseDTO> getDealsDueForSettlement(LocalDate date);
    List<SecuritiesDealResponseDTO> getDealsByCounterpartyAndStatus(Long cpId, DealStatus status);
    List<SecuritiesDealResponseDTO> getRepoDealsByCounterparty(Long cpId);
    BigDecimal getTotalSettledQuantityByInstrument(Long instrumentId);
}