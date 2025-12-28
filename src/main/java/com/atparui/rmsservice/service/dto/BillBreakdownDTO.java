package com.atparui.rmsservice.service.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for bill with item, tax, and discount breakdown.
 */
public class BillBreakdownDTO extends BillDTO {

    private List<BillItemBreakdownDTO> items;
    private List<BillTaxBreakdownDTO> taxes;
    private List<BillDiscountBreakdownDTO> discounts;
    private BigDecimal subtotal;
    private BigDecimal totalTax;
    private BigDecimal totalDiscount;
    private BigDecimal grandTotal;

    public List<BillItemBreakdownDTO> getItems() {
        return items;
    }

    public void setItems(List<BillItemBreakdownDTO> items) {
        this.items = items;
    }

    public List<BillTaxBreakdownDTO> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<BillTaxBreakdownDTO> taxes) {
        this.taxes = taxes;
    }

    public List<BillDiscountBreakdownDTO> getDiscounts() {
        return discounts;
    }

    public void setDiscounts(List<BillDiscountBreakdownDTO> discounts) {
        this.discounts = discounts;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(BigDecimal totalTax) {
        this.totalTax = totalTax;
    }

    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }

    /**
     * Inner DTO for bill item breakdown.
     */
    public static class BillItemBreakdownDTO extends BillItemDTO {
        // Additional breakdown details if needed
    }

    /**
     * Inner DTO for bill tax breakdown.
     */
    public static class BillTaxBreakdownDTO extends BillTaxDTO {
        // Additional breakdown details if needed
    }

    /**
     * Inner DTO for bill discount breakdown.
     */
    public static class BillDiscountBreakdownDTO extends BillDiscountDTO {
        // Additional breakdown details if needed
    }
}
