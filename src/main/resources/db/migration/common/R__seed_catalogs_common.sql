-- Unit measures
insert into core.unit_measure (id, code, name, allows_decimals, allowed_decimals)
values
    (gen_random_uuid(), 'UN', 'Unidad', false, 0),
    (gen_random_uuid(), 'KG', 'Kilogramo', true, 3),
    (gen_random_uuid(), 'LT', 'Litro', true, 3)
    on conflict (code) do update
                              set
                                  name = excluded.name,
                              allows_decimals = excluded.allows_decimals,
                              allowed_decimals = excluded.allowed_decimals;

-- Product status
insert into core.product_status (id, name, allows_sales, allows_purchases)
values
    (gen_random_uuid(), 'ACTIVE', true, true),
    (gen_random_uuid(), 'DISCONTINUED', false, false)
    on conflict (name) do update
                              set
                                  allows_sales = excluded.allows_sales,
                              allows_purchases = excluded.allows_purchases;

-- Product types
insert into core.product_type (id, name, description)
values
    (gen_random_uuid(), 'GENERAL', 'Producto normal'),
    (gen_random_uuid(), 'SERVICE', 'Servicio')
    on conflict (name) do update
                              set
                                  description = excluded.description;
