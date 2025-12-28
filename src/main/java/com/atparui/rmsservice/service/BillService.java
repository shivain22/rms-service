package com.atparui.rmsservice.service;

import com.atparui.rmsservice.domain.Bill;
import com.atparui.rmsservice.repository.BillDiscountRepository;
import com.atparui.rmsservice.repository.BillItemRepository;
import com.atparui.rmsservice.repository.BillRepository;
import com.atparui.rmsservice.repository.BillTaxRepository;
import com.atparui.rmsservice.repository.OrderRepository;
import com.atparui.rmsservice.repository.search.BillSearchRepository;
import com.atparui.rmsservice.service.dto.BillBreakdownDTO;
import com.atparui.rmsservice.service.dto.BillDTO;
import com.atparui.rmsservice.service.dto.BillGenerationRequestDTO;
import com.atparui.rmsservice.service.dto.DiscountApplicationRequestDTO;
import com.atparui.rmsservice.service.mapper.BillItemMapper;
import com.atparui.rmsservice.service.mapper.BillMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.atparui.rmsservice.domain.Bill}.
 */
@Service
@Transactional
public class BillService {

    private static final Logger LOG = LoggerFactory.getLogger(BillService.class);

    private final BillRepository billRepository;

    private final BillMapper billMapper;

    private final BillSearchRepository billSearchRepository;

    private final OrderRepository orderRepository;

    private final BillItemRepository billItemRepository;

    private final BillTaxRepository billTaxRepository;

    private final BillDiscountRepository billDiscountRepository;

    private final BillItemMapper billItemMapper;

    private final com.atparui.rmsservice.service.mapper.BillTaxMapper billTaxMapper;

    private final com.atparui.rmsservice.service.mapper.BillDiscountMapper billDiscountMapper;

    private final com.atparui.rmsservice.service.DiscountService discountService;

    public BillService(
        BillRepository billRepository,
        BillMapper billMapper,
        BillSearchRepository billSearchRepository,
        OrderRepository orderRepository,
        BillItemRepository billItemRepository,
        BillTaxRepository billTaxRepository,
        BillDiscountRepository billDiscountRepository,
        BillItemMapper billItemMapper,
        com.atparui.rmsservice.service.mapper.BillTaxMapper billTaxMapper,
        com.atparui.rmsservice.service.mapper.BillDiscountMapper billDiscountMapper,
        com.atparui.rmsservice.service.DiscountService discountService
    ) {
        this.billRepository = billRepository;
        this.billMapper = billMapper;
        this.billSearchRepository = billSearchRepository;
        this.orderRepository = orderRepository;
        this.billItemRepository = billItemRepository;
        this.billTaxRepository = billTaxRepository;
        this.billDiscountRepository = billDiscountRepository;
        this.billItemMapper = billItemMapper;
        this.billTaxMapper = billTaxMapper;
        this.billDiscountMapper = billDiscountMapper;
        this.discountService = discountService;
    }

    /**
     * Save a bill.
     *
     * @param billDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<BillDTO> save(BillDTO billDTO) {
        LOG.debug("Request to save Bill : {}", billDTO);
        return billRepository.save(billMapper.toEntity(billDTO)).flatMap(billSearchRepository::save).map(billMapper::toDto);
    }

    /**
     * Update a bill.
     *
     * @param billDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<BillDTO> update(BillDTO billDTO) {
        LOG.debug("Request to update Bill : {}", billDTO);
        return billRepository
            .save(billMapper.toEntity(billDTO).setIsPersisted())
            .flatMap(billSearchRepository::save)
            .map(billMapper::toDto);
    }

    /**
     * Partially update a bill.
     *
     * @param billDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<BillDTO> partialUpdate(BillDTO billDTO) {
        LOG.debug("Request to partially update Bill : {}", billDTO);

        return billRepository
            .findById(billDTO.getId())
            .map(existingBill -> {
                billMapper.partialUpdate(existingBill, billDTO);

                return existingBill;
            })
            .flatMap(billRepository::save)
            .flatMap(savedBill -> {
                billSearchRepository.save(savedBill);
                return Mono.just(savedBill);
            })
            .map(billMapper::toDto);
    }

    /**
     * Get all the bills.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<BillDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Bills");
        return billRepository.findAllBy(pageable).map(billMapper::toDto);
    }

    /**
     * Returns the number of bills available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return billRepository.count();
    }

    /**
     * Returns the number of bills available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return billSearchRepository.count();
    }

    /**
     * Get one bill by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<BillDTO> findOne(UUID id) {
        LOG.debug("Request to get Bill : {}", id);
        return billRepository.findById(id).map(billMapper::toDto);
    }

    /**
     * Delete the bill by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete Bill : {}", id);
        return billRepository.deleteById(id).then(billSearchRepository.deleteById(id));
    }

    /**
     * Search for the bill corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<BillDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Bills for query {}", query);
        return billSearchRepository.search(query, pageable).map(billMapper::toDto);
    }

    // jhipster-needle-service-impl-add-method - JHipster will add methods here

    /**
     * Find bills by branch ID and date range
     *
     * @param branchId the branch ID
     * @param startDate the start date
     * @param endDate the end date
     * @return the list of bill DTOs
     */
    @Transactional(readOnly = true)
    public Flux<BillDTO> findByBranchIdAndDateRange(UUID branchId, Instant startDate, Instant endDate) {
        LOG.debug("Request to find Bills by branch ID and date range : {} - {} to {}", branchId, startDate, endDate);
        return billRepository.findByBranchIdAndBillDateBetween(branchId, startDate, endDate).map(billMapper::toDto);
    }

    /**
     * Get bill with breakdown (items, taxes, discounts)
     *
     * @param id the bill ID
     * @return the bill breakdown DTO
     */
    @Transactional(readOnly = true)
    public Mono<BillBreakdownDTO> getBillBreakdown(UUID id) {
        LOG.debug("Request to get Bill breakdown : {}", id);
        return billRepository
            .findById(id)
            .map(billMapper::toDto)
            .flatMap(billDTO -> {
                BillBreakdownDTO breakdown = new BillBreakdownDTO();
                // Copy all fields from BillDTO
                breakdown.setId(billDTO.getId());
                breakdown.setBillNumber(billDTO.getBillNumber());
                breakdown.setBillDate(billDTO.getBillDate());
                breakdown.setSubtotal(billDTO.getSubtotal());
                breakdown.setTaxAmount(billDTO.getTaxAmount());
                breakdown.setDiscountAmount(billDTO.getDiscountAmount());
                breakdown.setServiceCharge(billDTO.getServiceCharge());
                breakdown.setTotalAmount(billDTO.getTotalAmount());
                breakdown.setAmountPaid(billDTO.getAmountPaid());
                breakdown.setAmountDue(billDTO.getAmountDue());
                breakdown.setStatus(billDTO.getStatus());
                breakdown.setNotes(billDTO.getNotes());
                breakdown.setOrder(billDTO.getOrder());
                breakdown.setBranch(billDTO.getBranch());
                breakdown.setCustomer(billDTO.getCustomer());

                return billItemRepository
                    .findByBill(id)
                    .map(billItem -> {
                        BillBreakdownDTO.BillItemBreakdownDTO itemDTO = new BillBreakdownDTO.BillItemBreakdownDTO();
                        com.atparui.rmsservice.service.dto.BillItemDTO baseDTO = billItemMapper.toDto(billItem);
                        itemDTO.setId(baseDTO.getId());
                        itemDTO.setItemName(baseDTO.getItemName());
                        itemDTO.setQuantity(baseDTO.getQuantity());
                        itemDTO.setUnitPrice(baseDTO.getUnitPrice());
                        itemDTO.setItemTotal(baseDTO.getItemTotal());
                        itemDTO.setBill(baseDTO.getBill());
                        itemDTO.setOrderItem(baseDTO.getOrderItem());
                        return itemDTO;
                    })
                    .collectList()
                    .flatMap(items -> {
                        breakdown.setItems(items);
                        return billTaxRepository
                            .findByBill(id)
                            .map(tax -> {
                                BillBreakdownDTO.BillTaxBreakdownDTO taxDTO = new BillBreakdownDTO.BillTaxBreakdownDTO();
                                com.atparui.rmsservice.service.dto.BillTaxDTO baseDTO = billTaxMapper.toDto(tax);
                                taxDTO.setId(baseDTO.getId());
                                taxDTO.setTaxName(baseDTO.getTaxName());
                                taxDTO.setTaxRate(baseDTO.getTaxRate());
                                taxDTO.setTaxableAmount(baseDTO.getTaxableAmount());
                                taxDTO.setTaxAmount(baseDTO.getTaxAmount());
                                taxDTO.setBill(baseDTO.getBill());
                                taxDTO.setTaxConfig(baseDTO.getTaxConfig());
                                return taxDTO;
                            })
                            .collectList();
                    })
                    .flatMap(taxes -> {
                        breakdown.setTaxes(taxes);
                        return billDiscountRepository
                            .findByBill(id)
                            .map(discount -> {
                                BillBreakdownDTO.BillDiscountBreakdownDTO discountDTO = new BillBreakdownDTO.BillDiscountBreakdownDTO();
                                com.atparui.rmsservice.service.dto.BillDiscountDTO baseDTO = billDiscountMapper.toDto(discount);
                                discountDTO.setId(baseDTO.getId());
                                discountDTO.setDiscountCode(baseDTO.getDiscountCode());
                                discountDTO.setDiscountType(baseDTO.getDiscountType());
                                discountDTO.setDiscountValue(baseDTO.getDiscountValue());
                                discountDTO.setDiscountAmount(baseDTO.getDiscountAmount());
                                discountDTO.setBill(baseDTO.getBill());
                                discountDTO.setDiscount(baseDTO.getDiscount());
                                return discountDTO;
                            })
                            .collectList();
                    })
                    .map(discounts -> {
                        breakdown.setDiscounts(discounts);
                        breakdown.setSubtotal(billDTO.getSubtotal());
                        breakdown.setTotalTax(billDTO.getTaxAmount());
                        breakdown.setTotalDiscount(billDTO.getDiscountAmount());
                        breakdown.setGrandTotal(billDTO.getTotalAmount());
                        return breakdown;
                    });
            });
    }

    /**
     * Generate a bill from an order
     *
     * @param request the bill generation request
     * @return the generated bill DTO
     */
    public Mono<BillDTO> generateBillFromOrder(BillGenerationRequestDTO request) {
        LOG.debug("Request to generate bill from order : {}", request);
        // TODO: Implement full bill generation logic with items, taxes, discounts
        return orderRepository
            .findById(request.getOrderId())
            .switchIfEmpty(Mono.error(new RuntimeException("Order not found")))
            .flatMap(order -> {
                BillDTO billDTO = new BillDTO();
                billDTO.setId(UUID.randomUUID());

                // Set nested DTOs with IDs
                if (order.getId() != null) {
                    com.atparui.rmsservice.service.dto.OrderDTO orderDTO = new com.atparui.rmsservice.service.dto.OrderDTO();
                    orderDTO.setId(order.getId());
                    billDTO.setOrder(orderDTO);
                }
                if (order.getBranchId() != null) {
                    com.atparui.rmsservice.service.dto.BranchDTO branchDTO = new com.atparui.rmsservice.service.dto.BranchDTO();
                    branchDTO.setId(order.getBranchId());
                    billDTO.setBranch(branchDTO);
                }
                if (order.getCustomerId() != null) {
                    com.atparui.rmsservice.service.dto.CustomerDTO customerDTO = new com.atparui.rmsservice.service.dto.CustomerDTO();
                    customerDTO.setId(order.getCustomerId());
                    billDTO.setCustomer(customerDTO);
                }

                billDTO.setBillDate(Instant.now());
                billDTO.setBillNumber("BILL-" + System.currentTimeMillis());
                billDTO.setSubtotal(order.getSubtotal() != null ? order.getSubtotal() : BigDecimal.ZERO);
                billDTO.setTaxAmount(order.getTaxAmount());
                billDTO.setDiscountAmount(order.getDiscountAmount());
                billDTO.setTotalAmount(order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO);
                billDTO.setAmountDue(billDTO.getTotalAmount());
                billDTO.setStatus("PENDING");
                return save(billDTO);
            });
    }

    /**
     * Apply a discount to a bill
     *
     * @param id the id of the bill
     * @param request the discount application request
     * @return the updated bill DTO
     */
    public Mono<BillDTO> applyDiscount(UUID id, DiscountApplicationRequestDTO request) {
        LOG.debug("Request to apply discount to bill : {} - {}", id, request);
        return billRepository
            .findById(id)
            .switchIfEmpty(Mono.error(new RuntimeException("Bill not found")))
            .flatMap(bill ->
                discountService
                    .findOne(request.getDiscountId())
                    .flatMap(discountDTO -> {
                        // Calculate discount amount
                        BigDecimal discountAmount = BigDecimal.ZERO;
                        BigDecimal subtotal = bill.getSubtotal() != null ? bill.getSubtotal() : BigDecimal.ZERO;
                        if ("PERCENTAGE".equals(discountDTO.getDiscountType())) {
                            discountAmount = subtotal.multiply(discountDTO.getDiscountValue()).divide(new BigDecimal("100"));
                        } else if ("FIXED_AMOUNT".equals(discountDTO.getDiscountType())) {
                            discountAmount = discountDTO.getDiscountValue();
                        }
                        BigDecimal currentDiscount = bill.getDiscountAmount() != null ? bill.getDiscountAmount() : BigDecimal.ZERO;
                        bill.setDiscountAmount(currentDiscount.add(discountAmount));
                        BigDecimal taxAmount = bill.getTaxAmount() != null ? bill.getTaxAmount() : BigDecimal.ZERO;
                        bill.setTotalAmount(subtotal.add(taxAmount).subtract(bill.getDiscountAmount()));
                        BigDecimal amountPaid = bill.getAmountPaid() != null ? bill.getAmountPaid() : BigDecimal.ZERO;
                        bill.setAmountDue(bill.getTotalAmount().subtract(amountPaid));
                        return billRepository.save(bill);
                    })
            )
            .flatMap(savedBill -> {
                billSearchRepository.save(savedBill);
                return Mono.just(savedBill);
            })
            .map(billMapper::toDto);
    }
}
