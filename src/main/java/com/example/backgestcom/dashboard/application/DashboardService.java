package com.example.backgestcom.dashboard.application;

import com.example.backgestcom.catalog.infra.ProductRepository;
import com.example.backgestcom.dashboard.api.dtos.DashboardMetricsResponse;
import com.example.backgestcom.inventory.infra.StockItemRepository;
import com.example.backgestcom.sales.domain.Sale;
import com.example.backgestcom.sales.domain.SaleItem;
import com.example.backgestcom.sales.domain.SaleStatus;
import com.example.backgestcom.sales.infra.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final StockItemRepository stockItemRepository;

    @Transactional(readOnly = true)
    public DashboardMetricsResponse getMetrics() {
        // 1. Fechas para filtrar "Hoy"
        var startOfDay = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant();

        // 2. Ventas de Hoy (Solo CONFIRMADAS)
        var todaySales = saleRepository.findAll().stream()
                .filter(s -> s.getStatus() == SaleStatus.CONFIRMED)
                .filter(s -> s.getConfirmedAt() != null && s.getConfirmedAt().isAfter(startOfDay))
                .toList();

        // Suma total de ventas (Facturación)
        BigDecimal totalRevenue = todaySales.stream()
                .map(Sale::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // --- NUEVO CÁLCULO: Ganancia Estimada ---
        BigDecimal totalCost = BigDecimal.ZERO;

        for (Sale sale : todaySales) {
            for (SaleItem item : sale.getItems()) {
                // Costo actual del producto * Cantidad vendida
                BigDecimal itemCost = item.getProduct().getCurrentCost()
                        .multiply(item.getQuantity());
                totalCost = totalCost.add(itemCost);
            }
        }

        BigDecimal netProfit = totalRevenue.subtract(totalCost);
        // ----------------------------------------

        long transactionCount = todaySales.size();
        long criticalStock = stockItemRepository.findAll().stream()
                .filter(stock -> stock.getOnHand().compareTo(stock.getCriticalThreshold()) <= 0)
                .count();

        // Ya no necesitamos activeProducts obligatoriamente, pero si quieres lo dejas
        long activeProducts = productRepository.count();

        return DashboardMetricsResponse.builder()
                .todaySalesTotal(totalRevenue)
                .todayNetProfit(netProfit) // <--- ASIGNAR
                .todayTransactions(transactionCount)
                .criticalStockItems(criticalStock)
                .activeProducts(activeProducts)
                .build();
    }
}