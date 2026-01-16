package com.example.backgestcom.dashboard.api.dtos;

import lombok.Builder;
import lombok.Value;
import java.math.BigDecimal;

@Value
@Builder
public class DashboardMetricsResponse {
    BigDecimal todaySalesTotal;
    BigDecimal todayNetProfit;
    long todayTransactions;
    long criticalStockItems;
    long activeProducts;
}