-- V3__insert_default_deposit.sql
-- Inserta el Depósito Central por defecto para evitar errores de 'No active deposit configured'

INSERT INTO core.deposit (id, name, description, is_active, created_at)
VALUES (
           gen_random_uuid(),                       -- Genera un UUID válido automáticamente
           'Depósito Central',                      -- Nombre
           'Almacenamiento principal de mercadería',-- Descripción
           true,                                    -- Activo
           NOW()                                    -- Fecha actual
       )
    ON CONFLICT (name) DO NOTHING;               -- Si ya existe (por nombre), no hace nada para no duplicar error