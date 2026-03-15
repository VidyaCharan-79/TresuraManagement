package com.tresura.dealingservice.service;

import com.tresura.dealingservice.dto.MMDealRequestDTO;
import com.tresura.dealingservice.dto.MMDealResponseDTO;
import com.tresura.dealingservice.entities.MMDeal;
import com.tresura.dealingservice.enums.BuySell;
import com.tresura.dealingservice.enums.DealStatus;
import com.tresura.dealingservice.exception.ResourceNotFoundException;
import com.tresura.dealingservice.repository.MMDealRepository;
import com.tresura.dealingservice.service.MMDealService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MMDealServiceImpl implements MMDealService {

    private final MMDealRepository mmDealRepository;

    // ── CRUD ─────────────────────────────────────────────────────────────────

    @Override
    public MMDealResponseDTO createDeal(MMDealRequestDTO dto) {
        validateMMDealDates(dto);
        MMDeal deal = mapToEntity(dto);
        MMDeal saved = mmDealRepository.save(deal);
        return mapToResponseDTO(saved);
    }

    @Override
    public MMDealResponseDTO getDealById(Long id) {
        MMDeal deal = findByIdOrThrow(id);
        return mapToResponseDTO(deal);
    }

    @Override
    public List<MMDealResponseDTO> getAllDeals() {
        return mmDealRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MMDealResponseDTO updateDeal(Long id, MMDealRequestDTO dto) {
        MMDeal existing = findByIdOrThrow(id);

        // Only NEW deals can be updated
        if (existing.getStatus() != DealStatus.NEW) {
            throw new IllegalArgumentException(
                    "Only deals in NEW status can be updated. " +
                            "Current status: " + existing.getStatus());
        }

        validateMMDealDates(dto);

        existing.setCpId(dto.getCpId());
        existing.setBuySell(dto.getBuySell());
        existing.setPrincipal(dto.getPrincipal());
        existing.setCurrency(dto.getCurrency());
        existing.setRate(dto.getRate());
        existing.setTradeDate(dto.getTradeDate());
        existing.setStartDate(dto.getStartDate());
        existing.setMaturityDate(dto.getMaturityDate());

        MMDeal updated = mmDealRepository.save(existing);
        return mapToResponseDTO(updated);
    }

    @Override
    public void deleteDeal(Long id) {
        MMDeal existing = findByIdOrThrow(id);

        // Only NEW or CANCELLED deals can be deleted
        if (existing.getStatus() != DealStatus.NEW
                && existing.getStatus() != DealStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Only deals in NEW or CANCELLED status can be deleted. " +
                            "Current status: " + existing.getStatus());
        }

        mmDealRepository.deleteById(id);
    }

    // ── Status Management ─────────────────────────────────────────────────────

    @Override
    public MMDealResponseDTO updateStatus(Long id, DealStatus newStatus) {
        MMDeal deal = findByIdOrThrow(id);
        validateStatusTransition(deal.getStatus(), newStatus);
        deal.setStatus(newStatus);
        MMDeal updated = mmDealRepository.save(deal);
        return mapToResponseDTO(updated);
    }

    // ── Filters ───────────────────────────────────────────────────────────────

    @Override
    public List<MMDealResponseDTO> getDealsByCounterparty(Long cpId) {
        return mmDealRepository.findByCpId(cpId)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<MMDealResponseDTO> getDealsByStatus(DealStatus status) {
        return mmDealRepository.findByStatus(status)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<MMDealResponseDTO> getDealsByCurrency(String currency) {
        return mmDealRepository.findByCurrency(currency.toUpperCase())
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<MMDealResponseDTO> getDealsByBuySell(BuySell buySell) {
        return mmDealRepository.findByBuySell(buySell)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<MMDealResponseDTO> getDealsByTradeDateRange(LocalDate fromDate,
                                                            LocalDate toDate) {
        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException(
                    "fromDate cannot be after toDate");
        }
        return mmDealRepository.findByTradeDateBetween(fromDate, toDate)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<MMDealResponseDTO> getDealsMaturingOn(LocalDate date) {
        return mmDealRepository.findByMaturityDateLessThanEqual(date)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<MMDealResponseDTO> getDealsByCounterpartyAndStatus(Long cpId,
                                                                   DealStatus status) {
        return mmDealRepository.findByCpIdAndStatus(cpId, status)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<MMDealResponseDTO> getMaturingDealsByStatus(LocalDate today,
                                                            LocalDate futureDate,
                                                            DealStatus status) {
        return mmDealRepository.findMaturingDealsByStatus(today, futureDate, status)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<MMDealResponseDTO> getDealsByCurrencyAndPrincipalRange(String currency,
                                                                       BigDecimal minAmount,
                                                                       BigDecimal maxAmount) {
        if (minAmount.compareTo(maxAmount) > 0) {
            throw new IllegalArgumentException(
                    "minAmount cannot be greater than maxAmount");
        }
        return mmDealRepository.findByCurrencyAndPrincipalRange(
                        currency.toUpperCase(), minAmount, maxAmount)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    // ── Private Helpers ───────────────────────────────────────────────────────

    private MMDeal findByIdOrThrow(Long id) {
        return mmDealRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "MMDeal", "id", id));
    }

    private void validateMMDealDates(MMDealRequestDTO dto) {
        if (dto.getStartDate().isBefore(dto.getTradeDate())) {
            throw new IllegalArgumentException(
                    "Start date cannot be before trade date");
        }
        if (dto.getMaturityDate().isBefore(dto.getStartDate())) {
            throw new IllegalArgumentException(
                    "Maturity date cannot be before start date");
        }
    }

    private void validateStatusTransition(DealStatus current, DealStatus next) {
        // Allowed transitions:
        // NEW -> CONFIRMED
        // NEW -> CANCELLED
        // CONFIRMED -> SETTLED
        // CONFIRMED -> CANCELLED
        boolean allowed = switch (current) {
            case NEW       -> next == DealStatus.CONFIRMED || next == DealStatus.CANCELLED;
            case CONFIRMED -> next == DealStatus.SETTLED   || next == DealStatus.CANCELLED;
            case SETTLED, CANCELLED -> false;
        };

        if (!allowed) {
            throw new IllegalArgumentException(
                    "Invalid status transition from " + current + " to " + next +
                            ". Allowed: NEW->CONFIRMED, NEW->CANCELLED, " +
                            "CONFIRMED->SETTLED, CONFIRMED->CANCELLED");
        }
    }

    private MMDeal mapToEntity(MMDealRequestDTO dto) {
        return MMDeal.builder()
                .cpId(dto.getCpId())
                .buySell(dto.getBuySell())
                .principal(dto.getPrincipal())
                .currency(dto.getCurrency().toUpperCase())
                .rate(dto.getRate())
                .tradeDate(dto.getTradeDate())
                .startDate(dto.getStartDate())
                .maturityDate(dto.getMaturityDate())
                .status(DealStatus.NEW)
                .build();
    }

    private MMDealResponseDTO mapToResponseDTO(MMDeal deal) {
        return MMDealResponseDTO.builder()
                .mmDealId(deal.getMmDealId())
                .cpId(deal.getCpId())
                .buySell(deal.getBuySell())
                .principal(deal.getPrincipal())
                .currency(deal.getCurrency())
                .rate(deal.getRate())
                .tradeDate(deal.getTradeDate())
                .startDate(deal.getStartDate())
                .maturityDate(deal.getMaturityDate())
                .status(deal.getStatus())
                .createdAt(deal.getCreatedAt())
                .updatedAt(deal.getUpdatedAt())
                .build();
    }
}