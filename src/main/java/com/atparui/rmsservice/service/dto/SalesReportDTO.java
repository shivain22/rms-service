package com.atparui.rmsservice.service.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * DTO for sales report.
 */
public class SalesReportDTO implements Serializable {

    private UUID branchId;
    private Instant startDate;
    private Instant endDate;
    private BigDecimal totalSales;
    private Integer totalOrders;
    private BigDecimal averageOrderValue;
    private List<SalesByCategoryDTO> salesByCategory;
    private List<SalesByDayDTO> salesByDay;

    public UUID getBranchId() {
        return branchId;
    }

    public void setBranchId(UUID branchId) {
        this.branchId = branchId;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    public BigDecimal getAverageOrderValue() {
        return averageOrderValue;
    }

    public void setAverageOrderValue(BigDecimal averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }

    public List<SalesByCategoryDTO> getSalesByCategory() {
        return salesByCategory;
    }

    public void setSalesByCategory(List<SalesByCategoryDTO> salesByCategory) {
        this.salesByCategory = salesByCategory;
    }

    public List<SalesByDayDTO> getSalesByDay() {
        return salesByDay;
    }

    public void setSalesByDay(List<SalesByDayDTO> salesByDay) {
        this.salesByDay = salesByDay;
    }

    /**
     * Inner DTO for sales by category.
     */
    public static class SalesByCategoryDTO implements Serializable {

        private UUID categoryId;
        private String categoryName;
        private BigDecimal totalSales;
        private Integer itemCount;

        public UUID getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(UUID categoryId) {
            this.categoryId = categoryId;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public BigDecimal getTotalSales() {
            return totalSales;
        }

        public void setTotalSales(BigDecimal totalSales) {
            this.totalSales = totalSales;
        }

        public Integer getItemCount() {
            return itemCount;
        }

        public void setItemCount(Integer itemCount) {
            this.itemCount = itemCount;
        }
    }

    /**
     * Inner DTO for sales by day.
     */
    public static class SalesByDayDTO implements Serializable {

        private Instant date;
        private BigDecimal totalSales;
        private Integer orderCount;

        public Instant getDate() {
            return date;
        }

        public void setDate(Instant date) {
            this.date = date;
        }

        public BigDecimal getTotalSales() {
            return totalSales;
        }

        public void setTotalSales(BigDecimal totalSales) {
            this.totalSales = totalSales;
        }

        public Integer getOrderCount() {
            return orderCount;
        }

        public void setOrderCount(Integer orderCount) {
            this.orderCount = orderCount;
        }
    }
}
