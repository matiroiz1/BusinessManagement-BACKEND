package com.example.backgestcom.sales.domain;

/**
 * Sale lifecycle status.
 * DRAFT: editable
 * CONFIRMED: stock has been reduced and totals are final
 * CANCELLED: optional future state (would require stock rollback)
 */
public enum SaleStatus {
    DRAFT,
    CONFIRMED,
    CANCELLED
}
