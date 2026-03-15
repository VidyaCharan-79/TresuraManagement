package com.tresura.dealingservice.service;

import com.tresura.dealingservice.dto.FXDealRequestDTO;
import com.tresura.dealingservice.dto.FXDealResponseDTO;
import com.tresura.dealingservice.entities.FXDeal;
import com.tresura.dealingservice.enums.DealStatus;
import com.tresura.dealingservice.enums.FXDealType;
import com.tresura.dealingservice.exception.ResourceNotFoundException;
import com.tresura.dealingservice.repository.FXDealRepository;
import com.tresura.dealingservice.service.FXDealService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FXDealServiceImpl implements FXDealService {

    private final FXDealRepository fxDealRepository;

    // ── CRUD ──────────────────────────────────────────────────────────────────

    @Override
    public FXDealResponseDTO createDeal(FXDealRequestDTO dto) {
        validateFXDealDates(dto);
        validateCurrencyPair(dto);
        FXDeal deal = mapToEntity(dto);
        FXDeal saved = fxDealRepository.save(deal);
        return mapToResponseDTO(saved);
    }

    @Override
    public FXDealResponseDTO getDealById(Long id) {
        FXDeal deal = findByIdOrThrow(id);
        return mapToResponseDTO(deal);
    }

    @Override
    public List<FXDealResponseDTO> getAllDeals() {
        return fxDealRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public FXDealResponseDTO updateDeal(Long id, FXDealRequestDTO dto) {
        FXDeal existing = findByIdOrThrow(id);

        if (existing.getStatus() != DealStatus.NEW) {
            throw new IllegalArgumentException(
                    "Only deals in NEW status can be updated. " +
                            "Current status: " + existing.getStatus());
        }

        validateFXDealDates(dto);
        validateCurrencyPair(dto);

        existing.setCpId(dto.getCpId());
        existing.setDealType(dto.getDealType());
        existing.setBuyCurrency(dto.getBuyCurrency().toUpperCase());
        existing.setSellCurrency(dto.getSellCurrency().toUpperCase());
        existing.setBuyAmount(dto.getBuyAmount());
        existing.setSellAmount(dto.getSellAmount());
        existing.setRate(dto.getRate());
        existing.setTradeDate(dto.getTradeDate());
        existing.setValueDate(dto.getValueDate());

        FXDeal updated = fxDealRepository.save(existing);
        return mapToResponseDTO(updated);
    }

    @Override
    public void deleteDeal(Long id) {
        FXDeal existing = findByIdOrThrow(id);

        if (existing.getStatus() != DealStatus.NEW
                && existing.getStatus() != DealStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Only deals in NEW or CANCELLED status can be deleted. " +
                            "Current status: " + existing.getStatus());
        }

        fxDealRepository.deleteById(id);
    }

    // ── Status Management ─────────────────────────────────────────────────────

    @Override
    public FXDealResponseDTO updateStatus(Long id, DealStatus newStatus) {
        FXDeal deal = findByIdOrThrow(id);
        validateStatusTransition(deal.getStatus(), newStatus);
        deal.setStatus(newStatus);
        FXDeal updated = fxDealRepository.save(deal);
        return mapToResponseDTO(updated);
    }

    // ── Filters ───────────────────────────────────────────────────────────────

    @Override
    public List<FXDealResponseDTO> getDealsByCounterparty(Long cpId) {
        return fxDealRepository.findByCpId(cpId)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<FXDealResponseDTO> getDealsByStatus(DealStatus status) {
        return fxDealRepository.findByStatus(status)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<FXDealResponseDTO> getDealsByDealType(FXDealType dealType) {
        return fxDealRepository.findByDealType(dealType)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<FXDealResponseDTO> getDealsByCurrencyPair(String buyCurrency,
                                                          String sellCurrency) {
        return fxDealRepository.findByBuyCurrencyAndSellCurrency(
                        buyCurrency.toUpperCase(), sellCurrency.toUpperCase())
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<FXDealResponseDTO> getDealsByTradeDateRange(LocalDate fromDate,
                                                            LocalDate toDate) {
        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException(
                    "fromDate cannot be after toDate");
        }
        return fxDealRepository.findByTradeDateBetween(fromDate, toDate)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<FXDealResponseDTO> getDealsByValueDateRange(LocalDate fromDate,
                                                            LocalDate toDate) {
        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException(
                    "fromDate cannot be after toDate");
        }
        return fxDealRepository.findByValueDateBetween(fromDate, toDate)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<FXDealResponseDTO> getDealsSettlingOn(LocalDate date) {
        return fxDealRepository.findByValueDateLessThanEqual(date)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<FXDealResponseDTO> getDealsByCounterpartyAndStatus(Long cpId,
                                                                   DealStatus status) {
        return fxDealRepository.findByCpIdAndStatus(cpId, status)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<FXDealResponseDTO> getActiveDealsByCurrencyPair(String buyCurrency,
                                                                String sellCurrency,
                                                                DealStatus status) {
        return fxDealRepository.findActiveDealsByCurrencyPair(
                        buyCurrency.toUpperCase(), sellCurrency.toUpperCase(), status)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    // ── Private Helpers ───────────────────────────────────────────────────────

    private FXDeal findByIdOrThrow(Long id) {
        return fxDealRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "FXDeal", "id", id));
    }

    private void validateFXDealDates(FXDealRequestDTO dto) {
        if (dto.getValueDate().isBefore(dto.getTradeDate())) {
            throw new IllegalArgumentException(
                    "Value date cannot be before trade date");
        }
    }

    private void validateCurrencyPair(FXDealRequestDTO dto) {
        if (dto.getBuyCurrency().equalsIgnoreCase(dto.getSellCurrency())) {
            throw new IllegalArgumentException(
                    "Buy currency and sell currency cannot be the same");
        }
    }

    private void validateStatusTransition(DealStatus current, DealStatus next) {
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

    private FXDeal mapToEntity(FXDealRequestDTO dto) {
        return FXDeal.builder()
                .cpId(dto.getCpId())
                .dealType(dto.getDealType())
                .buyCurrency(dto.getBuyCurrency().toUpperCase())
                .sellCurrency(dto.getSellCurrency().toUpperCase())
                .buyAmount(dto.getBuyAmount())
                .sellAmount(dto.getSellAmount())
                .rate(dto.getRate())
                .tradeDate(dto.getTradeDate())
                .valueDate(dto.getValueDate())
                .status(DealStatus.NEW)
                .build();
    }

    private FXDealResponseDTO mapToResponseDTO(FXDeal deal) {
        return FXDealResponseDTO.builder()
                .fxDealId(deal.getFxDealId())
                .cpId(deal.getCpId())
                .dealType(deal.getDealType())
                .buyCurrency(deal.getBuyCurrency())
                .sellCurrency(deal.getSellCurrency())
                .buyAmount(deal.getBuyAmount())
                .sellAmount(deal.getSellAmount())
                .rate(deal.getRate())
                .tradeDate(deal.getTradeDate())
                .valueDate(deal.getValueDate())
                .status(deal.getStatus())
                .createdAt(deal.getCreatedAt())
                .updatedAt(deal.getUpdatedAt())
                .build();
    }
}