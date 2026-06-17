-- P0 Baseline: extensions + updated_at trigger helpers
-- Ref: gym-platform-docs/architecture/data-model/README.md

CREATE EXTENSION IF NOT EXISTS pgcrypto;    -- gen_random_uuid(), hashing
CREATE EXTENSION IF NOT EXISTS btree_gist;  -- EXCLUDE constraints chống trùng giờ

-- Hàm set updated_at = now() khi UPDATE
CREATE OR REPLACE FUNCTION set_updated_at() RETURNS trigger AS $$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Tự gắn trigger trg_set_updated_at cho mọi bảng public có cột updated_at mà chưa có trigger.
-- Mỗi migration tạo bảng mới sẽ gọi: SELECT apply_updated_at_triggers();
CREATE OR REPLACE FUNCTION apply_updated_at_triggers() RETURNS void AS $$
DECLARE r record;
BEGIN
    FOR r IN
        SELECT c.table_name
        FROM information_schema.columns c
        WHERE c.table_schema = 'public'
          AND c.column_name = 'updated_at'
          AND NOT EXISTS (
              SELECT 1
              FROM pg_trigger t
              JOIN pg_class cl ON cl.oid = t.tgrelid
              JOIN pg_namespace n ON n.oid = cl.relnamespace
              WHERE n.nspname = 'public'
                AND cl.relname = c.table_name
                AND t.tgname = 'trg_set_updated_at')
    LOOP
        EXECUTE format(
            'CREATE TRIGGER trg_set_updated_at BEFORE UPDATE ON public.%I FOR EACH ROW EXECUTE FUNCTION set_updated_at()',
            r.table_name);
    END LOOP;
END;
$$ LANGUAGE plpgsql;
