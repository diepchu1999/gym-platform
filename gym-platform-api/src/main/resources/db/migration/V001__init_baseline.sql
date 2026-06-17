-- P0 Baseline: schema-per-module + extensions + updated_at helpers.
-- Microservices-ready: mỗi module 1 schema; KHÔNG FK chéo module (chỉ ID logic).
-- Ref: gym-platform-docs/architecture/data-model/README.md + module-schemas.md

-- Một schema cho mỗi module (com.gym.*)
CREATE SCHEMA IF NOT EXISTS identity;
CREATE SCHEMA IF NOT EXISTS branch;
CREATE SCHEMA IF NOT EXISTS staff;
CREATE SCHEMA IF NOT EXISTS member;
CREATE SCHEMA IF NOT EXISTS kyc;
CREATE SCHEMA IF NOT EXISTS membership;
CREATE SCHEMA IF NOT EXISTS contract;
CREATE SCHEMA IF NOT EXISTS payment;
CREATE SCHEMA IF NOT EXISTS finance;
CREATE SCHEMA IF NOT EXISTS checkin;
CREATE SCHEMA IF NOT EXISTS booking;
CREATE SCHEMA IF NOT EXISTS groupclass;
CREATE SCHEMA IF NOT EXISTS pt;
CREATE SCHEMA IF NOT EXISTS privateroom;
CREATE SCHEMA IF NOT EXISTS massage;
CREATE SCHEMA IF NOT EXISTS inventory;
CREATE SCHEMA IF NOT EXISTS equipment;
CREATE SCHEMA IF NOT EXISTS crm;
CREATE SCHEMA IF NOT EXISTS rating;
CREATE SCHEMA IF NOT EXISTS promotion;
CREATE SCHEMA IF NOT EXISTS notification;
CREATE SCHEMA IF NOT EXISTS audit;
CREATE SCHEMA IF NOT EXISTS messaging;

-- Extensions (public): dùng chung qua search_path
CREATE EXTENSION IF NOT EXISTS pgcrypto;    -- gen_random_uuid()
CREATE EXTENSION IF NOT EXISTS btree_gist;  -- EXCLUDE chống trùng giờ

-- updated_at trigger helper (đặt ở public, dùng cho mọi schema)
CREATE OR REPLACE FUNCTION public.set_updated_at() RETURNS trigger AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Gắn trigger cho mọi bảng (ở bất kỳ module schema) có cột updated_at mà chưa có trigger.
CREATE OR REPLACE FUNCTION public.apply_updated_at_triggers() RETURNS void AS $$
DECLARE r record;
BEGIN
    FOR r IN
        SELECT c.table_schema, c.table_name
        FROM information_schema.columns c
        WHERE c.column_name = 'updated_at'
          AND c.table_schema NOT IN ('pg_catalog','information_schema','public')
          AND NOT EXISTS (
              SELECT 1 FROM pg_trigger t
              JOIN pg_class cl ON cl.oid = t.tgrelid
              JOIN pg_namespace n ON n.oid = cl.relnamespace
              WHERE n.nspname = c.table_schema
                AND cl.relname = c.table_name
                AND t.tgname = 'trg_set_updated_at')
    LOOP
        EXECUTE format(
            'CREATE TRIGGER trg_set_updated_at BEFORE UPDATE ON %I.%I FOR EACH ROW EXECUTE FUNCTION public.set_updated_at()',
            r.table_schema, r.table_name);
    END LOOP;
END;
$$ LANGUAGE plpgsql;
