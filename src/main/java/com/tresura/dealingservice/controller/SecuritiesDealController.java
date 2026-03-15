package com.tresura.dealingservice.controller;

import com.tresura.dealingservice.dto.ApiResponse;
import com.tresura.dealingservice.dto.SecuritiesDealRequestDTO;
import com.tresura.dealingservice.dto.SecuritiesDealResponseDTO;
import com.tresura.dealingservice.enums.DealStatus;
import com.tresura.dealingservice.enums.SecuritiesSide;
import com.tresura.dealingservice.service.SecuritiesDealService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/securities-deals")
@RequiredArgsConstructor
public class SecuritiesDealController {

    private final SecuritiesDealService securitiesDealService;

    // ── POST /api/v1/securities-deals ────────────────────────────────────────
    // Create a new Securities deal
    @PostMapping
    public ResponseEntity<ApiResponse<SecuritiesDealResponseDTO>> createDeal(
            @Valid @RequestBody SecuritiesDealRequestDTO requestDTO) {

        SecuritiesDealResponseDTO response = securitiesDealService.createDeal(requestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Securities Deal created successfully", response));
    }

    // ── GET /api/v1/securities-deals/{id} ────────────────────────────────────
    // Get a single Securities deal by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SecuritiesDealResponseDTO>> getDealById(
            @PathVariable Long id) {

        SecuritiesDealResponseDTO response = securitiesDealService.getDealById(id);
        return ResponseEntity.ok(
                ApiResponse.success("Securities Deal fetched successfully", response));
    }

    // ── GET /api/v1/securities-deals ─────────────────────────────────────────
    // Get all Securities deals
    @GetMapping
    public ResponseEntity<ApiResponse<List<SecuritiesDealResponseDTO>>> getAllDeals() {

        List<SecuritiesDealResponseDTO> response = securitiesDealService.getAllDeals();
        return ResponseEntity.ok(
                ApiResponse.success(
                        "All Securities Deals fetched successfully", response));
    }

    // ── PUT /api/v1/securities-deals/{id} ────────────────────────────────────
    // Update a Securities deal (only NEW status)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SecuritiesDealResponseDTO>> updateDeal(
            @PathVariable Long id,
            @RequestBody SecuritiesDealRequestDTO requestDTO) {

        SecuritiesDealResponseDTO response =
                securitiesDealService.updateDeal(id, requestDTO);
        return ResponseEntity.ok(
                ApiResponse.success("Securities Deal updated successfully", response));
    }

    // ── DELETE /api/v1/securities-deals/{id} ─────────────────────────────────
    // Delete a Securities deal (only NEW or CANCELLED)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDeal(
            @PathVariable Long id) {

        securitiesDealService.deleteDeal(id);
        return ResponseEntity.ok(
                ApiResponse.success("Securities Deal deleted successfully"));
    }

    // ── PATCH /api/v1/securities-deals/{id}/status?newStatus=CONFIRMED ────────
    // Update Securities deal status
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<SecuritiesDealResponseDTO>> updateStatus(
            @PathVariable Long id,
            @RequestParam DealStatus newStatus) {

        SecuritiesDealResponseDTO response =
                securitiesDealService.updateStatus(id, newStatus);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Securities Deal status updated to " + newStatus, response));
    }

    // ── GET /api/v1/securities-deals/counterparty/{cpId} ─────────────────────
    // Get all Securities deals for a counterparty
    @GetMapping("/counterparty/{cpId}")
    public ResponseEntity<ApiResponse<List<SecuritiesDealResponseDTO>>> getDealsByCounterparty(
            @PathVariable Long cpId) {

        List<SecuritiesDealResponseDTO> response =
                securitiesDealService.getDealsByCounterparty(cpId);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Securities Deals for counterparty " + cpId, response));
    }

    // ── GET /api/v1/securities-deals/status/{status} ──────────────────────────
    // Get Securities deals by status
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<SecuritiesDealResponseDTO>>> getDealsByStatus(
            @PathVariable DealStatus status) {

        List<SecuritiesDealResponseDTO> response =
                securitiesDealService.getDealsByStatus(status);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Securities Deals with status " + status, response));
    }

    // ── GET /api/v1/securities-deals/side/{side} ──────────────────────────────
    // Get Securities deals by side (BUY / SELL / REPO / REVERSE_REPO)
    @GetMapping("/side/{side}")
    public ResponseEntity<ApiResponse<List<SecuritiesDealResponseDTO>>> getDealsBySide(
            @PathVariable SecuritiesSide side) {

        List<SecuritiesDealResponseDTO> response =
                securitiesDealService.getDealsBySide(side);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Securities Deals with side " + side, response));
    }

    // ── GET /api/v1/securities-deals/instrument/{instrumentId} ────────────────
    // Get all Securities deals for an instrument
    @GetMapping("/instrument/{instrumentId}")
    public ResponseEntity<ApiResponse<List<SecuritiesDealResponseDTO>>> getDealsByInstrument(
            @PathVariable Long instrumentId) {

        List<SecuritiesDealResponseDTO> response =
                securitiesDealService.getDealsByInstrument(instrumentId);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Securities Deals for instrument " + instrumentId, response));
    }

    // ── GET /api/v1/securities-deals/trade-date
    //        ?fromDate=2025-01-01&toDate=2025-12-31 ──────────────────────────
    // Get Securities deals by trade date range
    @GetMapping("/trade-date")
    public ResponseEntity<ApiResponse<List<SecuritiesDealResponseDTO>>> getDealsByTradeDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        List<SecuritiesDealResponseDTO> response =
                securitiesDealService.getDealsByTradeDateRange(fromDate, toDate);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Securities Deals traded between " + fromDate + " and " + toDate,
                        response));
    }

    // ── GET /api/v1/securities-deals/settlement?date=2025-06-30 ──────────────
    // Get Securities deals due for settlement on or before a date
    @GetMapping("/settlement")
    public ResponseEntity<ApiResponse<List<SecuritiesDealResponseDTO>>> getDealsDueForSettlement(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<SecuritiesDealResponseDTO> response =
                securitiesDealService.getDealsDueForSettlement(date);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Securities Deals due for settlement on or before " + date,
                        response));
    }

    // ── GET /api/v1/securities-deals/counterparty/{cpId}/status/{status} ──────
    // Get Securities deals by counterparty and status
    @GetMapping("/counterparty/{cpId}/status/{status}")
    public ResponseEntity<ApiResponse<List<SecuritiesDealResponseDTO>>> getDealsByCounterpartyAndStatus(
            @PathVariable Long cpId,
            @PathVariable DealStatus status) {

        List<SecuritiesDealResponseDTO> response =
                securitiesDealService.getDealsByCounterpartyAndStatus(cpId, status);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Securities Deals for CP " + cpId +
                                " with status " + status, response));
    }

    // ── GET /api/v1/securities-deals/repo/counterparty/{cpId} ────────────────
    // Get all REPO and REVERSE_REPO deals for a counterparty
    @GetMapping("/repo/counterparty/{cpId}")
    public ResponseEntity<ApiResponse<List<SecuritiesDealResponseDTO>>> getRepoDealsByCounterparty(
            @PathVariable Long cpId) {

        List<SecuritiesDealResponseDTO> response =
                securitiesDealService.getRepoDealsByCounterparty(cpId);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Repo Deals for counterparty " + cpId, response));
    }

    // ── GET /api/v1/securities-deals/instrument/{instrumentId}/settled-quantity ─
    // Get total settled quantity for an instrument
    @GetMapping("/instrument/{instrumentId}/settled-quantity")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalSettledQuantity(
            @PathVariable Long instrumentId) {

        BigDecimal totalQty =
                securitiesDealService.getTotalSettledQuantityByInstrument(instrumentId);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Total settled quantity for instrument " + instrumentId,
                        totalQty));
    }
}
