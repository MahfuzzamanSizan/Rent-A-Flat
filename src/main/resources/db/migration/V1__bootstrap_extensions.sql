-- ═══════════════════════════════════════════════════════════════
-- RentEase — V1: Bootstrap Extensions & Custom Types
-- ═══════════════════════════════════════════════════════════════

-- UUID generation support
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
-- pg_trgm for ILIKE full-text search on listing titles/descriptions
CREATE EXTENSION IF NOT EXISTS "pg_trgm";
-- btree_gin for composite GIN indexes
CREATE EXTENSION IF NOT EXISTS "btree_gin";

-- ── Enum Types ────────────────────────────────────────────────────────────────
-- Stored as VARCHAR in JPA (@Enumerated(EnumType.STRING)) but we define
-- PostgreSQL CHECK constraints for data integrity.

-- All CHECK constraints are added inline on the column definitions in V2+.
-- This migration just sets up extensions.

-- Helper function: auto-set updated_at on any UPDATE
CREATE OR REPLACE FUNCTION trigger_set_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;