INSERT INTO core.app_user (
    id, username, email, password_hash, role, status, created_at, last_login_at
)
SELECT
    '00000000-0000-0000-0000-000000000001'::uuid,
    'admin',
    NULL,
    '$2a$10$4f/MyFiQh9mCE8N6PfZT3uL6J4SEQrFs8tekH8u6wRfYgs45PcBd2',
    'ADMIN',
    'ACTIVE',
    now(),
    NULL
    WHERE NOT EXISTS (
  SELECT 1 FROM core.app_user WHERE username = 'admin'
);
