package com.tresura.dealingservice.service;

import com.tresura.dealingservice.dto.SecuritiesDealRequestDTO;
import com.tresura.dealingservice.dto.SecuritiesDealResponseDTO;
import com.tresura.dealingservice.entities.SecuritiesDeal;
import com.tresura.dealingservice.enums.DealStatus;
import com.tresura.dealingservice.enums.SecuritiesSide;
import com.tresura.dealingservice.exception.ResourceNotFoundException;
import com.tresura.dealingservice.repository.SecuritiesDealRepository;
import com.tresura.dealingservice.service.SecuritiesDealService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SecuritiesDealServiceImpl implements SecuritiesDealService {

    private final SecuritiesDealRepository securitiesDealRepository;

    // ── CRUD ──────────────────────────────────────────────────────────────────

    @Override
    public SecuritiesDealResponseDTO createDeal(SecuritiesDealRequestDTO dto) {
        validateSecuritiesDealDates(dto);
        SecuritiesDeal deal = mapToEntity(dto);
        SecuritiesDeal saved = securitiesDealRepository.save(deal);
        return mapToResponseDTO(saved);
    }

    @Override
    public SecuritiesDealResponseDTO getDealById(Long id) {
        SecuritiesDeal deal = findByIdOrThrow(id);
        return mapToResponseDTO(deal);
    }

    @Override
    public List<SecuritiesDealResponseDTO> getAllDeals() {
        return securitiesDealRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SecuritiesDealResponseDTO updateDeal(Long id, SecuritiesDealRequestDTO dto) {
        SecuritiesDeal existing = findByIdOrThrow(id);

        if (existing.getStatus() != DealStatus.NEW) {
            throw new IllegalArgumentException(
                    "Only deals in NEW status can be updated. " +
                            "Current status: " + existing.getStatus());
        }

        validateSecuritiesDealDates(dto);

        existing.setCpId(dto.getCpId());
        existing.setInstrumentId(dto.getInstrumentId());
        existing.setSide(dto.getSide());
        existing.setQuantity(dto.getQuantity());
        existing.setPrice(dto.getPrice());
        existing.setTradeDate(dto.getTradeDate());
        existing.setSettlementDate(dto.getSettlementDate());

        SecuritiesDeal updated = securitiesDealRepository.save(existing);
        return mapToResponseDTO(updated);
    }

    @Override
    public void deleteDeal(Long id) {
        SecuritiesDeal existing = findByIdOrThrow(id);

        if (existing.getStatus() != DealStatus.NEW
                && existing.getStatus() != DealStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Only deals in NEW or CANCELLED status can be deleted. " +
                            "Current status: " + existing.getStatus());
        }

        securitiesDealRepository.deleteById(id);
    }

    // ── Status Management ─────────────────────────────────────────────────────

    @Override
    public SecuritiesDealResponseDTO updateStatus(Long id, DealStatus newStatus) {
        SecuritiesDeal deal = findByIdOrThrow(id);
        validateStatusTransition(deal.getStatus(), newStatus);
        deal.setStatus(newStatus);
        SecuritiesDeal updated = securitiesDealRepository.save(deal);
        return mapToResponseDTO(updated);
    }

    // ── Filters ───────────────────────────────────────────────────────────────

    @Override
    public List<SecuritiesDealResponseDTO> getDealsByCounterparty(Long cpId) {
        return securitiesDealRepository.findByCpId(cpId)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<SecuritiesDealResponseDTO> getDealsByStatus(DealStatus status) {
        return securitiesDealRepository.findByStatus(status)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<SecuritiesDealResponseDTO> getDealsBySide(SecuritiesSide side) {
        return securitiesDealRepository.findBySide(side)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<SecuritiesDealResponseDTO> getDealsByInstrument(Long instrumentId) {
        return securitiesDealRepository.findByInstrumentId(instrumentId)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<SecuritiesDealResponseDTO> getDealsByTradeDateRange(LocalDate fromDate,
                                                                    LocalDate toDate) {
        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException(
                    "fromDate cannot be after toDate");
        }
        return securitiesDealRepository.findByTradeDateBetween(fromDate, toDate)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<SecuritiesDealResponseDTO> getDealsDueForSettlement(LocalDate date) {
        return securitiesDealRepository.findBySettlementDateLessThanEqual(date)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<SecuritiesDealResponseDTO> getDealsByCounterpartyAndStatus(Long cpId,
                                                                           DealStatus status) {
        return securitiesDealRepository.findByCpIdAndStatus(cpId, status)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public List<SecuritiesDealResponseDTO> getRepoDealsByCounterparty(Long cpId) {
        return securitiesDealRepository.findRepoDealsByCounterparty(cpId)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public BigDecimal getTotalSettledQuantityByInstrument(Long instrumentId) {
        return securitiesDealRepository.sumSettledQuantityByInstrument(instrumentId);
    }

    // ── Private Helpers ───────────────────────────────────────────────────────

    private SecuritiesDeal findByIdOrThrow(Long id) {
        return securitiesDealRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "SecuritiesDeal", "id", id));
    }

    private void validateSecuritiesDealDates(SecuritiesDealRequestDTO dto) {
        if (dto.getSettlementDate().isBefore(dto.getTradeDate())) {
            throw new IllegalArgumentException(
                    "Settlement date cannot be before trade date");
        }
    }

    private void validateStatusTransition(DealStatus current, DealStatus next) {
        boolean allowed = switch (current) {
            case NEW -> next == DealStatus.CONFIRMED || next == DealStatus.CANCELLED;
            case CONFIRMED -> next == DealStatus.SETTLED || next == DealStatus.CANCELLED;
            case SETTLED, CANCELLED -> false;
        };

        if (!allowed) {
            throw new IllegalArgumentException(
                    "Invalid status transition from " + current + " to " + next +
                            ". Allowed: NEW->CONFIRMED, NEW->CANCELLED, " +
                            "CONFIRMED->SETTLED, CONFIRMED->CANCELLED");
        }
    }

    private SecuritiesDeal mapToEntity(SecuritiesDealRequestDTO dto) {
        return SecuritiesDeal.builder()
                .cpId(dto.getCpId())
                .instrumentId(dto.getInstrumentId())
                .side(dto.getSide())
                .quantity(dto.getQuantity())
                .price(dto.getPrice())
                .tradeDate(dto.getTradeDate())
                .settlementDate(dto.getSettlementDate())
                .status(DealStatus.NEW)
                .build();
    }

    private SecuritiesDealResponseDTO mapToResponseDTO(SecuritiesDeal deal) {
        BigDecimal notional = deal.getQuantity().multiply(deal.getPrice());
        return SecuritiesDealResponseDTO.builder()
                .secDealId(deal.getSecDealId())
                .cpId(deal.getCpId())
                .instrumentId(deal.getInstrumentId())
                .side(deal.getSide())
                .quantity(deal.getQuantity())
                .price(deal.getPrice())
                .notionalValue(notional)
                .tradeDate(deal.getTradeDate())
                .settlementDate(deal.getSettlementDate())
                .status(deal.getStatus())
                .createdAt(deal.getCreatedAt())
                .updatedAt(deal.getUpdatedAt())
                .build();
    }
}