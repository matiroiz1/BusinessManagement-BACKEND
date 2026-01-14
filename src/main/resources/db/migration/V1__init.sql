-- V1__init.sql
-- Schema
CREATE SCHEMA IF NOT EXISTS core;

-- =========================
-- SECURITY: app_user
-- =========================
CREATE TABLE IF NOT EXISTS core.app_user (
                                             id              uuid PRIMARY KEY,
                                             username        varchar(60)  NOT NULL,
    email           varchar(120),
    password_hash   varchar(255) NOT NULL,
    role            varchar(30)  NOT NULL,
    status          varchar(20)  NOT NULL,
    created_at      timestamptz  NOT NULL,
    last_login_at   timestamptz
    );

CREATE UNIQUE INDEX IF NOT EXISTS uq_user_username ON core.app_user(username);
CREATE UNIQUE INDEX IF NOT EXISTS uq_user_email ON core.app_user(email);

-- =========================
-- CATALOG
-- =========================
CREATE TABLE IF NOT EXISTS core.product_type (
                                                 id          uuid PRIMARY KEY,
                                                 name        varchar(80) NOT NULL UNIQUE,
    description text
    );

CREATE TABLE IF NOT EXISTS core.product_status (
                                                   id              uuid PRIMARY KEY,
                                                   name            varchar(80) NOT NULL UNIQUE,
    allows_sales    boolean NOT NULL DEFAULT true,
    allows_purchases boolean NOT NULL DEFAULT true
    );

CREATE TABLE IF NOT EXISTS core.unit_measure (
                                                 id              uuid PRIMARY KEY,
                                                 code            varchar(10) NOT NULL UNIQUE,
    name            varchar(80) NOT NULL,
    allows_decimals boolean NOT NULL,
    allowed_decimals smallint NOT NULL
    );

CREATE TABLE IF NOT EXISTS core.product (
                                            id                 uuid PRIMARY KEY,
                                            sku                varchar(60)  NOT NULL UNIQUE,
    name               varchar(160) NOT NULL,
    description        text,
    barcode            varchar(80),
    product_type_id    uuid NOT NULL,
    product_status_id  uuid NOT NULL,
    unit_measure_id    uuid NOT NULL,
    current_sale_price numeric(18,2) NOT NULL DEFAULT 0,
    current_cost       numeric(18,2),
    is_active          boolean NOT NULL DEFAULT true,
    created_at         timestamptz NOT NULL,
    updated_at         timestamptz,

    CONSTRAINT fk_product_type
    FOREIGN KEY (product_type_id) REFERENCES core.product_type(id),
    CONSTRAINT fk_product_status
    FOREIGN KEY (product_status_id) REFERENCES core.product_status(id),
    CONSTRAINT fk_product_unit_measure
    FOREIGN KEY (unit_measure_id) REFERENCES core.unit_measure(id)
    );

CREATE INDEX IF NOT EXISTS ix_product_product_type_id ON core.product(product_type_id);
CREATE INDEX IF NOT EXISTS ix_product_product_status_id ON core.product(product_status_id);
CREATE INDEX IF NOT EXISTS ix_product_unit_measure_id ON core.product(unit_measure_id);

-- =========================
-- INVENTORY
-- =========================
CREATE TABLE IF NOT EXISTS core.deposit (
                                            id          uuid PRIMARY KEY,
                                            name        varchar(80) NOT NULL UNIQUE,
    description text,
    is_active   boolean NOT NULL DEFAULT true,
    created_at  timestamptz NOT NULL
    );

CREATE TABLE IF NOT EXISTS core.stock_item (
                                               id                 uuid PRIMARY KEY,
                                               product_id         uuid NOT NULL,
                                               deposit_id         uuid NOT NULL,
                                               on_hand            numeric(18,2) NOT NULL DEFAULT 0,
    critical_threshold numeric(18,2) NOT NULL DEFAULT 0,
    minimum_threshold  numeric(18,2),
    updated_at         timestamptz,

    CONSTRAINT fk_stock_item_product
    FOREIGN KEY (product_id) REFERENCES core.product(id),
    CONSTRAINT fk_stock_item_deposit
    FOREIGN KEY (deposit_id) REFERENCES core.deposit(id),
    CONSTRAINT uq_stock_product_deposit
    UNIQUE (product_id, deposit_id)
    );

CREATE INDEX IF NOT EXISTS ix_stock_item_product_id ON core.stock_item(product_id);
CREATE INDEX IF NOT EXISTS ix_stock_item_deposit_id ON core.stock_item(deposit_id);

CREATE TABLE IF NOT EXISTS core.stock_movement (
                                                   id                 uuid PRIMARY KEY,
                                                   created_at          timestamptz NOT NULL,
                                                   type               varchar(40) NOT NULL,
    product_id         uuid NOT NULL,
    deposit_id         uuid NOT NULL,
    quantity           numeric(18,2) NOT NULL,
    reference_type     varchar(30),
    reference_id       uuid,
    performed_by_user_id uuid,
    reason             text,
    notes              text,

    CONSTRAINT fk_stock_movement_product
    FOREIGN KEY (product_id) REFERENCES core.product(id),
    CONSTRAINT fk_stock_movement_deposit
    FOREIGN KEY (deposit_id) REFERENCES core.deposit(id)
    );

CREATE INDEX IF NOT EXISTS ix_stock_movement_product_id ON core.stock_movement(product_id);
CREATE INDEX IF NOT EXISTS ix_stock_movement_deposit_id ON core.stock_movement(deposit_id);
CREATE INDEX IF NOT EXISTS ix_stock_movement_created_at ON core.stock_movement(created_at);
CREATE INDEX IF NOT EXISTS ix_stock_movement_ref ON core.stock_movement(reference_type, reference_id);
CREATE INDEX IF NOT EXISTS ix_stock_movement_performed_by_user_id ON core.stock_movement(performed_by_user_id);

-- =========================
-- SALES
-- =========================
CREATE TABLE IF NOT EXISTS core.sale (
                                         id                 uuid PRIMARY KEY,
                                         status             varchar(20) NOT NULL,
    sold_by_user_id    uuid,
    customer_name      varchar(160),
    customer_document  varchar(40),
    subtotal           numeric(18,2) NOT NULL DEFAULT 0,
    total_discount     numeric(18,2) NOT NULL DEFAULT 0,
    total              numeric(18,2) NOT NULL DEFAULT 0,
    notes              text,
    created_at         timestamptz NOT NULL,
    confirmed_at       timestamptz,
    updated_at         timestamptz
    );

CREATE INDEX IF NOT EXISTS ix_sale_status ON core.sale(status);
CREATE INDEX IF NOT EXISTS ix_sale_sold_by_user_id ON core.sale(sold_by_user_id);
CREATE INDEX IF NOT EXISTS ix_sale_created_at ON core.sale(created_at);

CREATE TABLE IF NOT EXISTS core.sale_item (
                                              id              uuid PRIMARY KEY,
                                              sale_id         uuid NOT NULL,
                                              product_id      uuid NOT NULL,
                                              quantity        numeric(18,2) NOT NULL,
    unit_price      numeric(18,2) NOT NULL,
    discount_amount numeric(18,2) NOT NULL DEFAULT 0,
    line_subtotal   numeric(18,2) NOT NULL DEFAULT 0,
    line_total      numeric(18,2) NOT NULL DEFAULT 0,

    CONSTRAINT fk_sale_item_sale
    FOREIGN KEY (sale_id) REFERENCES core.sale(id) ON DELETE CASCADE,
    CONSTRAINT fk_sale_item_product
    FOREIGN KEY (product_id) REFERENCES core.product(id)
    );

CREATE INDEX IF NOT EXISTS ix_sale_item_sale_id ON core.sale_item(sale_id);
CREATE INDEX IF NOT EXISTS ix_sale_item_product_id ON core.sale_item(product_id);

-- =========================
-- PURCHASE / GOODS RECEIPT
-- =========================
CREATE TABLE IF NOT EXISTS core.goods_receipt (
                                                  id                 uuid PRIMARY KEY,
                                                  status             varchar(20) NOT NULL,
    received_by_user_id uuid NOT NULL,
    confirmed_by_user_id uuid,
    notes              text,
    created_at         timestamptz NOT NULL,
    confirmed_at       timestamptz,
    updated_at         timestamptz NOT NULL
    );

CREATE INDEX IF NOT EXISTS ix_goods_receipt_status ON core.goods_receipt(status);
CREATE INDEX IF NOT EXISTS ix_goods_receipt_received_by_user_id ON core.goods_receipt(received_by_user_id);
CREATE INDEX IF NOT EXISTS ix_goods_receipt_confirmed_by_user_id ON core.goods_receipt(confirmed_by_user_id);
CREATE INDEX IF NOT EXISTS ix_goods_receipt_created_at ON core.goods_receipt(created_at);

CREATE TABLE IF NOT EXISTS core.goods_receipt_item (
                                                       id              uuid PRIMARY KEY,
                                                       goods_receipt_id uuid NOT NULL,
                                                       product_id      uuid NOT NULL,
                                                       quantity        numeric(19,4) NOT NULL,

    CONSTRAINT fk_goods_receipt_item_gr
    FOREIGN KEY (goods_receipt_id) REFERENCES core.goods_receipt(id) ON DELETE CASCADE,
    CONSTRAINT fk_goods_receipt_item_product
    FOREIGN KEY (product_id) REFERENCES core.product(id)
    );

CREATE INDEX IF NOT EXISTS ix_goods_receipt_item_gr_id ON core.goods_receipt_item(goods_receipt_id);
CREATE INDEX IF NOT EXISTS ix_goods_receipt_item_product_id ON core.goods_receipt_item(product_id);
