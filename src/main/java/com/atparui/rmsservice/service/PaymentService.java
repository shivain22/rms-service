package com.atparui.rmsservice.service;

import com.atparui.rmsservice.repository.PaymentRepository;
import com.atparui.rmsservice.repository.search.PaymentSearchRepository;
import com.atparui.rmsservice.service.dto.PartialPaymentRequestDTO;
import com.atparui.rmsservice.service.dto.PaymentDTO;
import com.atparui.rmsservice.service.dto.PaymentRequestDTO;
import com.atparui.rmsservice.service.dto.PaymentSummaryDTO;
import com.atparui.rmsservice.service.dto.RefundRequestDTO;
import com.atparui.rmsservice.service.mapper.PaymentMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.atparui.rmsservice.domain.Payment}.
 */
@Service
@Transactional
public class PaymentService {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;

    private final PaymentMapper paymentMapper;

    private final PaymentSearchRepository paymentSearchRepository;

    public PaymentService(
        PaymentRepository paymentRepository,
        PaymentMapper paymentMapper,
        PaymentSearchRepository paymentSearchRepository
    ) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.paymentSearchRepository = paymentSearchRepository;
    }

    /**
     * Save a payment.
     *
     * @param paymentDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<PaymentDTO> save(PaymentDTO paymentDTO) {
        LOG.debug("Request to save Payment : {}", paymentDTO);
        return paymentRepository.save(paymentMapper.toEntity(paymentDTO)).flatMap(paymentSearchRepository::save).map(paymentMapper::toDto);
    }

    /**
     * Update a payment.
     *
     * @param paymentDTO the entity to save.
     * @return the persisted entity.
     */
    public Mono<PaymentDTO> update(PaymentDTO paymentDTO) {
        LOG.debug("Request to update Payment : {}", paymentDTO);
        return paymentRepository
            .save(paymentMapper.toEntity(paymentDTO).setIsPersisted())
            .flatMap(paymentSearchRepository::save)
            .map(paymentMapper::toDto);
    }

    /**
     * Partially update a payment.
     *
     * @param paymentDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<PaymentDTO> partialUpdate(PaymentDTO paymentDTO) {
        LOG.debug("Request to partially update Payment : {}", paymentDTO);

        return paymentRepository
            .findById(paymentDTO.getId())
            .map(existingPayment -> {
                paymentMapper.partialUpdate(existingPayment, paymentDTO);

                return existingPayment;
            })
            .flatMap(paymentRepository::save)
            .flatMap(savedPayment -> {
                paymentSearchRepository.save(savedPayment);
                return Mono.just(savedPayment);
            })
            .map(paymentMapper::toDto);
    }

    /**
     * Get all the payments.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<PaymentDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Payments");
        return paymentRepository.findAllBy(pageable).map(paymentMapper::toDto);
    }

    /**
     * Returns the number of payments available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return paymentRepository.count();
    }

    /**
     * Returns the number of payments available in search repository.
     *
     */
    public Mono<Long> searchCount() {
        return paymentSearchRepository.count();
    }

    /**
     * Get one payment by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<PaymentDTO> findOne(UUID id) {
        LOG.debug("Request to get Payment : {}", id);
        return paymentRepository.findById(id).map(paymentMapper::toDto);
    }

    /**
     * Delete the payment by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(UUID id) {
        LOG.debug("Request to delete Payment : {}", id);
        return paymentRepository.deleteById(id).then(paymentSearchRepository.deleteById(id));
    }

    /**
     * Search for the payment corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<PaymentDTO> search(String query, Pageable pageable) {
        LOG.debug("Request to search for a page of Payments for query {}", query);
        return paymentSearchRepository.search(query, pageable).map(paymentMapper::toDto);
    }

    // jhipster-needle-service-impl-add-method - JHipster will add methods here

    /**
     * Find payments by bill ID
     *
     * @param billId the bill ID
     * @return the list of payment DTOs
     */
    @Transactional(readOnly = true)
    public Flux<PaymentDTO> findByBillId(UUID billId) {
        LOG.debug("Request to find Payments by bill ID : {}", billId);
        return paymentRepository.findByBillId(billId).map(paymentMapper::toDto);
    }

    /**
     * Process a payment for a bill
     *
     * @param request the payment request
     * @return the created payment DTO
     */
    public Mono<PaymentDTO> processPayment(PaymentRequestDTO request) {
        LOG.debug("Request to process payment : {}", request);
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setId(UUID.randomUUID());

        // Set nested BillDTO with ID
        if (request.getBillId() != null) {
            com.atparui.rmsservice.service.dto.BillDTO billDTO = new com.atparui.rmsservice.service.dto.BillDTO();
            billDTO.setId(request.getBillId());
            paymentDTO.setBill(billDTO);
        }

        // Set nested PaymentMethodDTO with ID
        if (request.getPaymentMethodId() != null) {
            com.atparui.rmsservice.service.dto.PaymentMethodDTO paymentMethodDTO =
                new com.atparui.rmsservice.service.dto.PaymentMethodDTO();
            paymentMethodDTO.setId(request.getPaymentMethodId());
            paymentDTO.setPaymentMethod(paymentMethodDTO);
        }

        paymentDTO.setAmount(request.getAmount());
        paymentDTO.setTransactionId(request.getTransactionId());
        paymentDTO.setPaymentDate(Instant.now());
        paymentDTO.setStatus("COMPLETED");
        paymentDTO.setPaymentNumber("PAY-" + System.currentTimeMillis());
        paymentDTO.setNotes(request.getNotes());
        return save(paymentDTO);
    }

    /**
     * Process a partial payment for a bill
     *
     * @param request the partial payment request
     * @return the created payment DTO
     */
    public Mono<PaymentDTO> processPartialPayment(PartialPaymentRequestDTO request) {
        LOG.debug("Request to process partial payment : {}", request);
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setId(UUID.randomUUID());

        // Set nested BillDTO with ID
        if (request.getBillId() != null) {
            com.atparui.rmsservice.service.dto.BillDTO billDTO = new com.atparui.rmsservice.service.dto.BillDTO();
            billDTO.setId(request.getBillId());
            paymentDTO.setBill(billDTO);
        }

        // Set nested PaymentMethodDTO with ID
        if (request.getPaymentMethodId() != null) {
            com.atparui.rmsservice.service.dto.PaymentMethodDTO paymentMethodDTO =
                new com.atparui.rmsservice.service.dto.PaymentMethodDTO();
            paymentMethodDTO.setId(request.getPaymentMethodId());
            paymentDTO.setPaymentMethod(paymentMethodDTO);
        }

        paymentDTO.setAmount(request.getAmount());
        paymentDTO.setTransactionId(request.getTransactionId());
        paymentDTO.setPaymentDate(Instant.now());
        paymentDTO.setStatus("PARTIAL");
        paymentDTO.setPaymentNumber("PAY-" + System.currentTimeMillis());
        paymentDTO.setNotes(request.getNotes());
        return save(paymentDTO);
    }

    /**
     * Process a refund for a payment
     *
     * @param paymentId the id of the payment
     * @param request the refund request
     * @return the refunded payment DTO
     */
    public Mono<PaymentDTO> processRefund(UUID paymentId, RefundRequestDTO request) {
        LOG.debug("Request to process refund : {} - {}", paymentId, request);
        return paymentRepository
            .findById(paymentId)
            .switchIfEmpty(Mono.error(new RuntimeException("Payment not found")))
            .map(paymentMapper::toDto)
            .flatMap(originalPaymentDTO -> {
                PaymentDTO refundDTO = new PaymentDTO();
                refundDTO.setId(UUID.randomUUID());

                // Copy bill from original payment
                refundDTO.setBill(originalPaymentDTO.getBill());

                // Copy payment method from original payment
                refundDTO.setPaymentMethod(originalPaymentDTO.getPaymentMethod());

                refundDTO.setAmount(request.getRefundAmount().negate());
                refundDTO.setTransactionId(
                    "REFUND-" +
                    (originalPaymentDTO.getTransactionId() != null ? originalPaymentDTO.getTransactionId() : paymentId.toString())
                );
                refundDTO.setPaymentDate(Instant.now());
                refundDTO.setStatus("REFUNDED");
                refundDTO.setRefundReason(request.getRefundReason());
                refundDTO.setRefundedAt(Instant.now());
                refundDTO.setPaymentNumber("REFUND-" + System.currentTimeMillis());
                refundDTO.setNotes(request.getRefundReason());
                return save(refundDTO);
            });
    }

    /**
     * Get payment summary for a branch
     *
     * @param branchId the branch ID
     * @param startDate the start date
     * @param endDate the end date
     * @return the payment summary DTO
     */
    @Transactional(readOnly = true)
    public Mono<PaymentSummaryDTO> getPaymentSummary(UUID branchId, Instant startDate, Instant endDate) {
        LOG.debug("Request to get payment summary : {} - {} to {}", branchId, startDate, endDate);
        // TODO: Implement full payment summary logic with aggregation and branch filtering
        return paymentRepository
            .findAll()
            .filter(
                payment ->
                    payment.getPaymentDate() != null &&
                    payment.getPaymentDate().isAfter(startDate) &&
                    payment.getPaymentDate().isBefore(endDate)
            )
            .map(paymentMapper::toDto)
            .collectList()
            .map(payments -> {
                PaymentSummaryDTO summary = new PaymentSummaryDTO();
                summary.setBranchId(branchId);

                BigDecimal totalAmount = payments
                    .stream()
                    .map(PaymentDTO::getAmount)
                    .filter(amount -> amount != null && amount.compareTo(BigDecimal.ZERO) > 0)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                summary.setTotalAmount(totalAmount);
                summary.setTotalTransactions(Integer.valueOf(payments.size()));

                // Group by payment method
                Map<UUID, PaymentSummaryDTO.PaymentMethodSummaryDTO> methodMap = new HashMap<>();
                for (PaymentDTO payment : payments) {
                    if (
                        payment.getAmount() != null &&
                        payment.getAmount().compareTo(BigDecimal.ZERO) > 0 &&
                        payment.getPaymentMethod() != null
                    ) {
                        UUID methodId = payment.getPaymentMethod().getId();
                        PaymentSummaryDTO.PaymentMethodSummaryDTO methodSummary = methodMap.get(methodId);
                        if (methodSummary == null) {
                            methodSummary = new PaymentSummaryDTO.PaymentMethodSummaryDTO();
                            methodSummary.setPaymentMethodId(methodId);
                            methodSummary.setPaymentMethodName(
                                payment.getPaymentMethod().getMethodName() != null ? payment.getPaymentMethod().getMethodName() : "UNKNOWN"
                            );
                            methodSummary.setTotalAmount(BigDecimal.ZERO);
                            methodSummary.setTransactionCount(0);
                            methodMap.put(methodId, methodSummary);
                        }
                        methodSummary.setTotalAmount(methodSummary.getTotalAmount().add(payment.getAmount()));
                        methodSummary.setTransactionCount(methodSummary.getTransactionCount() + 1);
                    }
                }

                summary.setMethodSummaries(new java.util.ArrayList<>(methodMap.values()));
                return summary;
            });
    }
}
