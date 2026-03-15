package com.tresura.dealingservice.controller;

import com.tresura.dealingservice.dto.ApiResponse;
import com.tresura.dealingservice.dto.FXDealRequestDTO;
import com.tresura.dealingservice.dto.FXDealResponseDTO;
import com.tresura.dealingservice.enums.DealStatus;
import com.tresura.dealingservice.enums.FXDealType;
import com.tresura.dealingservice.service.FXDealService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/fx-deals")
@RequiredArgsConstructor
public class FXDealController {

    private final FXDealService fxDealService;

    // ── POST /api/v1/fx-deals ────────────────────────────────────────────────
    // Create a new FX deal
    @PostMapping
    public ResponseEntity<ApiResponse<FXDealResponseDTO>> createDeal(
            @Valid @RequestBody FXDealRequestDTO requestDTO) {

        FXDealResponseDTO response = fxDealService.createDeal(requestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("FX Deal created successfully", response));
    }

    // ── GET /api/v1/fx-deals/{id} ────────────────────────────────────────────
    // Get a single FX deal by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FXDealResponseDTO>> getDealById(
            @PathVariable Long id) {

        FXDealResponseDTO response = fxDealService.getDealById(id);
        return ResponseEntity.ok(
                ApiResponse.success("FX Deal fetched successfully", response));
    }

    // ── GET /api/v1/fx-deals ─────────────────────────────────────────────────
    // Get all FX deals
    @GetMapping
    public ResponseEntity<ApiResponse<List<FXDealResponseDTO>>> getAllDeals() {

        List<FXDealResponseDTO> response = fxDealService.getAllDeals();
        return ResponseEntity.ok(
                ApiResponse.success("All FX Deals fetched successfully", response));
    }

    // ── PUT /api/v1/fx-deals/{id} ────────────────────────────────────────────
    // Update an FX deal (only NEW status)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FXDealResponseDTO>> updateDeal(
            @PathVariable Long id,
            @Valid @RequestBody FXDealRequestDTO requestDTO) {

        FXDealResponseDTO response = fxDealService.updateDeal(id, requestDTO);
        return ResponseEntity.ok(
                ApiResponse.success("FX Deal updated successfully", response));
    }

    // ── DELETE /api/v1/fx-deals/{id} ─────────────────────────────────────────
    // Delete an FX deal (only NEW or CANCELLED)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDeal(
            @PathVariable Long id) {

        fxDealService.deleteDeal(id);
        return ResponseEntity.ok(
                ApiResponse.success("FX Deal deleted successfully"));
    }

    // ── PATCH /api/v1/fx-deals/{id}/status?newStatus=CONFIRMED ───────────────
    // Update FX deal status
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<FXDealResponseDTO>> updateStatus(
            @PathVariable Long id,
            @RequestParam DealStatus newStatus) {

        FXDealResponseDTO response = fxDealService.updateStatus(id, newStatus);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "FX Deal status updated to " + newStatus, response));
    }

    // ── GET /api/v1/fx-deals/counterparty/{cpId} ─────────────────────────────
    // Get all FX deals for a counterparty
    @GetMapping("/counterparty/{cpId}")
    public ResponseEntity<ApiResponse<List<FXDealResponseDTO>>> getDealsByCounterparty(
            @PathVariable Long cpId) {

        List<FXDealResponseDTO> response = fxDealService.getDealsByCounterparty(cpId);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "FX Deals for counterparty " + cpId, response));
    }

    // ── GET /api/v1/fx-deals/status/{status} ─────────────────────────────────
    // Get FX deals by status
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<FXDealResponseDTO>>> getDealsByStatus(
            @PathVariable DealStatus status) {

        List<FXDealResponseDTO> response = fxDealService.getDealsByStatus(status);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "FX Deals with status " + status, response));
    }

    // ── GET /api/v1/fx-deals/type/{dealType} ─────────────────────────────────
    // Get FX deals by deal type (SPOT / FORWARD / SWAP)
    @GetMapping("/type/{dealType}")
    public ResponseEntity<ApiResponse<List<FXDealResponseDTO>>> getDealsByDealType(
            @PathVariable FXDealType dealType) {

        List<FXDealResponseDTO> response = fxDealService.getDealsByDealType(dealType);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "FX Deals of type " + dealType, response));
    }

    // ── GET /api/v1/fx-deals/currency-pair?buyCurrency=USD&sellCurrency=INR ───
    // Get FX deals by currency pair
    @GetMapping("/currency-pair")
    public ResponseEntity<ApiResponse<List<FXDealResponseDTO>>> getDealsByCurrencyPair(
            @RequestParam String buyCurrency,
            @RequestParam String sellCurrency) {

        List<FXDealResponseDTO> response =
                fxDealService.getDealsByCurrencyPair(buyCurrency, sellCurrency);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "FX Deals for pair " +
                                buyCurrency.toUpperCase() + "/" + sellCurrency.toUpperCase(),
                        response));
    }

    // ── GET /api/v1/fx-deals/trade-date?fromDate=2025-01-01&toDate=2025-12-31 ─
    // Get FX deals by trade date range
    @GetMapping("/trade-date")
    public ResponseEntity<ApiResponse<List<FXDealResponseDTO>>> getDealsByTradeDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        List<FXDealResponseDTO> response =
                fxDealService.getDealsByTradeDateRange(fromDate, toDate);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "FX Deals traded between " + fromDate + " and " + toDate,
                        response));
    }

    // ── GET /api/v1/fx-deals/value-date?fromDate=2025-01-01&toDate=2025-12-31 ─
    // Get FX deals by value date range
    @GetMapping("/value-date")
    public ResponseEntity<ApiResponse<List<FXDealResponseDTO>>> getDealsByValueDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        List<FXDealResponseDTO> response =
                fxDealService.getDealsByValueDateRange(fromDate, toDate);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "FX Deals settling between " + fromDate + " and " + toDate,
                        response));
    }

    // ── GET /api/v1/fx-deals/settling?date=2025-06-30 ────────────────────────
    // Get FX deals settling on or before a date
    @GetMapping("/settling")
    public ResponseEntity<ApiResponse<List<FXDealResponseDTO>>> getDealsSettlingOn(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<FXDealResponseDTO> response = fxDealService.getDealsSettlingOn(date);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "FX Deals settling on or before " + date, response));
    }

    // ── GET /api/v1/fx-deals/counterparty/{cpId}/status/{status} ─────────────
    // Get FX deals by counterparty and status
    @GetMapping("/counterparty/{cpId}/status/{status}")
    public ResponseEntity<ApiResponse<List<FXDealResponseDTO>>> getDealsByCounterpartyAndStatus(
            @PathVariable Long cpId,
            @PathVariable DealStatus status) {

        List<FXDealResponseDTO> response =
                fxDealService.getDealsByCounterpartyAndStatus(cpId, status);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "FX Deals for CP " + cpId + " with status " + status, response));
    }

    // ── GET /api/v1/fx-deals/active-pair
    //        ?buyCurrency=USD&sellCurrency=INR&status=CONFIRMED ─────────────────
    // Get active FX deals for a currency pair
    @GetMapping("/active-pair")
    public ResponseEntity<ApiResponse<List<FXDealResponseDTO>>> getActiveDealsByCurrencyPair(
            @RequestParam String buyCurrency,
            @RequestParam String sellCurrency,
            @RequestParam DealStatus status) {

        List<FXDealResponseDTO> response =
                fxDealService.getActiveDealsByCurrencyPair(
                        buyCurrency, sellCurrency, status);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Active FX Deals for " +
                                buyCurrency.toUpperCase() + "/" + sellCurrency.toUpperCase(),
                        response));
    }
}