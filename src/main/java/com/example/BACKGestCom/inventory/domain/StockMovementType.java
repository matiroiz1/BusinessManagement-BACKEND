package com.example.BACKGestCom.inventory.domain;

/**
 * Enumeration representing the different types of inventory transactions.
 * Used for traceability in the Stock Movement history (Kardex).
 */
public enum StockMovementType {

    /** Reduction of stock due to a customer sale. */
    SALE_OUT,

    /** Increase of stock due to a purchase from a supplier. */
    PURCHASE_IN,

    /** Increase of stock when a customer returns a previously sold product. */
    RETURN_IN,

    /** Manual increase of stock (e.g., found items, donations received). */
    ADJUSTMENT_IN,

    /** Manual reduction of stock (e.g., damaged goods, expired products, loss). */
    ADJUSTMENT_OUT,

    /** * Stock correction triggered by a physical inventory audit
     * to match system records with actual shelf count.
     */
    INVENTORY_COUNT_ADJUSTMENT
}