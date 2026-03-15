package com.tresura.dealingservice.controller;

import com.tresura.dealingservice.dto.ApiResponse;
import com.tresura.dealingservice.dto.MMDealRequestDTO;
import com.tresura.dealingservice.dto.MMDealResponseDTO;
import com.tresura.dealingservice.enums.BuySell;
import com.tresura.dealingservice.enums.DealStatus;
import com.tresura.dealingservice.service.MMDealService;
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
@RequestMapping("/api/v1/mm-deals")
@RequiredArgsConstructor
public class MMDealController {

    private final MMDealService mmDealService;

    // ── POST /api/v1/mm-deals ────────────────────────────────────────────────
    // Create a new Money Market deal
    @PostMapping
    public ResponseEntity<ApiResponse<MMDealResponseDTO>> createDeal(
            @Valid @RequestBody MMDealRequestDTO requestDTO) {

        MMDealResponseDTO response = mmDealService.createDeal(requestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("MM Deal created successfully", response));
    }

    // ── GET /api/v1/mm-deals/{id} ────────────────────────────────────────────
    // Get a single MM deal by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MMDealResponseDTO>> getDealById(
            @PathVariable Long id) {

        MMDealResponseDTO response = mmDealService.getDealById(id);
        return ResponseEntity.ok(
                ApiResponse.success("MM Deal fetched successfully", response));
    }

    // ── GET /api/v1/mm-deals ─────────────────────────────────────────────────
    // Get all MM deals
    @GetMapping
    public ResponseEntity<ApiResponse<List<MMDealResponseDTO>>> getAllDeals() {

        List<MMDealResponseDTO> response = mmDealService.getAllDeals();
        return ResponseEntity.ok(
                ApiResponse.success("All MM Deals fetched successfully", response));
    }

    // ── PUT /api/v1/mm-deals/{id} ────────────────────────────────────────────
    // Update an existing MM deal (only NEW status)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MMDealResponseDTO>> updateDeal(
            @PathVariable Long id,
            @Valid @RequestBody MMDealRequestDTO requestDTO) {

        MMDealResponseDTO response = mmDealService.updateDeal(id, requestDTO);
        return ResponseEntity.ok(
                ApiResponse.success("MM Deal updated successfully", response));
    }

    // ── DELETE /api/v1/mm-deals/{id} ─────────────────────────────────────────
    // Delete an MM deal (only NEW or CANCELLED status)
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDeal(
            @PathVariable Long id) {

        mmDealService.deleteDeal(id);
        return ResponseEntity.ok(
                ApiResponse.success("MM Deal deleted successfully"));
    }

    // ── PATCH /api/v1/mm-deals/{id}/status?newStatus=CONFIRMED ───────────────
    // Update deal status
    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<MMDealResponseDTO>> updateStatus(
            @PathVariable Long id,
            @RequestParam DealStatus newStatus) {

        MMDealResponseDTO response = mmDealService.updateStatus(id, newStatus);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "MM Deal status updated to " + newStatus, response));
    }

    // ── GET /api/v1/mm-deals/counterparty/{cpId} ─────────────────────────────
    // Get all MM deals for a counterparty
    @GetMapping("/counterparty/{cpId}")
    public ResponseEntity<ApiResponse<List<MMDealResponseDTO>>> getDealsByCounterparty(
            @PathVariable Long cpId) {

        List<MMDealResponseDTO> response = mmDealService.getDealsByCounterparty(cpId);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "MM Deals for counterparty " + cpId, response));
    }

    // ── GET /api/v1/mm-deals/status/{status} ─────────────────────────────────
    // Get all MM deals by status
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<MMDealResponseDTO>>> getDealsByStatus(
            @PathVariable DealStatus status) {

        List<MMDealResponseDTO> response = mmDealService.getDealsByStatus(status);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "MM Deals with status " + status, response));
    }

    // ── GET /api/v1/mm-deals/currency/{currency} ──────────────────────────────
    // Get all MM deals by currency
    @GetMapping("/currency/{currency}")
    public ResponseEntity<ApiResponse<List<MMDealResponseDTO>>> getDealsByCurrency(
            @PathVariable String currency) {

        List<MMDealResponseDTO> response = mmDealService.getDealsByCurrency(currency);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "MM Deals in currency " + currency.toUpperCase(), response));
    }

    // ── GET /api/v1/mm-deals/direction/{buySell} ──────────────────────────────
    // Get all MM deals by direction (BORROW or LEND)
    @GetMapping("/direction/{buySell}")
    public ResponseEntity<ApiResponse<List<MMDealResponseDTO>>> getDealsByBuySell(
            @PathVariable BuySell buySell) {

        List<MMDealResponseDTO> response = mmDealService.getDealsByBuySell(buySell);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "MM Deals with direction " + buySell, response));
    }

    // ── GET /api/v1/mm-deals/trade-date?fromDate=2025-01-01&toDate=2025-12-31 ─
    // Get MM deals by trade date range
    @GetMapping("/trade-date")
    public ResponseEntity<ApiResponse<List<MMDealResponseDTO>>> getDealsByTradeDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        List<MMDealResponseDTO> response =
                mmDealService.getDealsByTradeDateRange(fromDate, toDate);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "MM Deals between " + fromDate + " and " + toDate, response));
    }

    // ── GET /api/v1/mm-deals/maturing?date=2025-12-31 ────────────────────────
    // Get MM deals maturing on or before a date
    @GetMapping("/maturing")
    public ResponseEntity<ApiResponse<List<MMDealResponseDTO>>> getDealsMaturingOn(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<MMDealResponseDTO> response = mmDealService.getDealsMaturingOn(date);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "MM Deals maturing on or before " + date, response));
    }

    // ── GET /api/v1/mm-deals/counterparty/{cpId}/status/{status} ─────────────
    // Get MM deals by counterparty and status
    @GetMapping("/counterparty/{cpId}/status/{status}")
    public ResponseEntity<ApiResponse<List<MMDealResponseDTO>>> getDealsByCounterpartyAndStatus(
            @PathVariable Long cpId,
            @PathVariable DealStatus status) {

        List<MMDealResponseDTO> response =
                mmDealService.getDealsByCounterpartyAndStatus(cpId, status);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "MM Deals for CP " + cpId + " with status " + status, response));
    }

    // ── GET /api/v1/mm-deals/maturing-by-status
    //        ?today=2025-01-01&futureDate=2025-03-31&status=CONFIRMED ───────────
    // Get maturing deals filtered by status
    @GetMapping("/maturing-by-status")
    public ResponseEntity<ApiResponse<List<MMDealResponseDTO>>> getMaturingDealsByStatus(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate today,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate futureDate,
            @RequestParam DealStatus status) {

        List<MMDealResponseDTO> response =
                mmDealService.getMaturingDealsByStatus(today, futureDate, status);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Maturing MM Deals with status " + status, response));
    }

    // ── GET /api/v1/mm-deals/currency-principal-range
    //        ?currency=USD&minAmount=100000&maxAmount=5000000 ──────────────────
    // Get MM deals by currency and principal range
    @GetMapping("/currency-principal-range")
    public ResponseEntity<ApiResponse<List<MMDealResponseDTO>>> getDealsByCurrencyAndPrincipalRange(
            @RequestParam String currency,
            @RequestParam BigDecimal minAmount,
            @RequestParam BigDecimal maxAmount) {

        List<MMDealResponseDTO> response =
                mmDealService.getDealsByCurrencyAndPrincipalRange(
                        currency, minAmount, maxAmount);
        return ResponseEntity.ok(
                ApiResponse.success(
                        "MM Deals in " + currency.toUpperCase() +
                                " between " + minAmount + " and " + maxAmount, response));
    }
}