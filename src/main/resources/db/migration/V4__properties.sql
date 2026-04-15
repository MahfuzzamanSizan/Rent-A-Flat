-- ═══════════════════════════════════════════════════════════════
-- RentEase — V4: Properties (Listings)
-- ═══════════════════════════════════════════════════════════════

CREATE TABLE IF NOT EXISTS properties (
                                          id               UUID          PRIMARY KEY DEFAULT uuid_generate_v4(),
    owner_id         UUID          NOT NULL
    REFERENCES users(id) ON DELETE CASCADE,
    title            VARCHAR(200)  NOT NULL,
    description      TEXT          NOT NULL,
    area_id          UUID          NOT NULL
    REFERENCES areas(id),
    full_address     TEXT,
    latitude         DECIMAL(10,8),
    longitude        DECIMAL(11,8),

    -- Pricing
    rent_amount      DECIMAL(10,2) NOT NULL,
    is_negotiable    BOOLEAN       NOT NULL DEFAULT FALSE,
    security_deposit DECIMAL(10,2),

    -- Specs
    property_type    VARCHAR(20)   NOT NULL
    CHECK (property_type IN ('APARTMENT','SUBLET','MESS','BACHELOR','FAMILY','OFFICE','SHOP')),
    bedrooms         SMALLINT,
    bathrooms        SMALLINT,
    floor_number     SMALLINT,
    total_floors     SMALLINT,
    size_sqft        DECIMAL(8,2),
    available_from   DATE,

    -- Amenities & rules (JSONB for flexible schema)
    -- Example: {"ac":true,"gas":true,"lift":false,"parking":true,"cctv":false,"generator":true}
    amenities        JSONB,
    house_rules      TEXT,
    preferred_tenant VARCHAR(20) CHECK (preferred_tenant IN ('bachelor','family','both')),

    -- Media
    video_url        TEXT,

    -- Lifecycle
    status           VARCHAR(20)   NOT NULL DEFAULT 'PENDING'
    CHECK (status IN ('PENDING','APPROVED','REJECTED','RENTED','EXPIRED')),
    rejection_reason TEXT,
    approved_at      TIMESTAMPTZ,

    -- Boost
    is_boosted       BOOLEAN       NOT NULL DEFAULT FALSE,
    boost_until      TIMESTAMPTZ,

    -- Analytics (Redis-backed counters batch-flushed here)
    views_count      INT           NOT NULL DEFAULT 0,
    inquiries_count  INT           NOT NULL DEFAULT 0,
    shortlists_count INT           NOT NULL DEFAULT 0,

    -- Audit
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ,
    created_by   UUID,
    updated_by   UUID,
    deleted_at   TIMESTAMPTZ,

    CONSTRAINT fk_property_owner FOREIGN KEY (owner_id) REFERENCES users(id),
    CONSTRAINT fk_property_area  FOREIGN KEY (area_id)  REFERENCES areas(id)
    );

-- ── Property photos (one-to-many via element collection table) ────────────────

CREATE TABLE IF NOT EXISTS property_photos (
                                               property_id UUID NOT NULL
                                               REFERENCES properties(id) ON DELETE CASCADE,
    photo_url   TEXT NOT NULL,
    sort_order  SMALLINT NOT NULL DEFAULT 0,
    PRIMARY KEY (property_id, photo_url),
    CONSTRAINT fk_photo_property FOREIGN KEY (property_id) REFERENCES properties(id)
    );

CREATE INDEX idx_photos_property ON property_photos (property_id);

-- ── Indexes ───────────────────────────────────────────────────────────────────

-- Core search indexes
CREATE INDEX idx_prop_owner     ON properties (owner_id);
CREATE INDEX idx_prop_area      ON properties (area_id);
CREATE INDEX idx_prop_status    ON properties (status);
CREATE INDEX idx_prop_type      ON properties (property_type);
CREATE INDEX idx_prop_rent      ON properties (rent_amount);
CREATE INDEX idx_prop_available ON properties (available_from);
CREATE INDEX idx_prop_deleted   ON properties (deleted_at) WHERE deleted_at IS NULL;

-- Partial index for live listings only (heavily used in search queries)
CREATE INDEX idx_prop_live ON properties (area_id, rent_amount, created_at)
    WHERE status = 'APPROVED' AND deleted_at IS NULL;

-- Boosted listings served first in area search
CREATE INDEX idx_prop_boosted ON properties (is_boosted, boost_until)
    WHERE is_boosted = TRUE;

-- GIN index on JSONB amenities for queries like amenities @> '{"ac": true}'
CREATE INDEX idx_prop_amenities ON properties USING GIN (amenities);

-- Full-text GIN index on title + description for keyword search
CREATE INDEX idx_prop_fts ON properties
    USING GIN (to_tsvector('english', title || ' ' || COALESCE(description, '')));

-- Trigram index on title for ILIKE search
CREATE INDEX idx_prop_title_trgm ON properties USING GIN (title gin_trgm_ops);

CREATE TRIGGER trg_properties_updated_at
    BEFORE UPDATE ON properties
    FOR EACH ROW EXECUTE FUNCTION trigger_set_updated_at();

-- ── Tenant shortlists (many-to-many) ─────────────────────────────────────────

CREATE TABLE IF NOT EXISTS tenant_shortlists (
                                                 tenant_id   UUID NOT NULL REFERENCES users(id)      ON DELETE CASCADE,
    property_id UUID NOT NULL REFERENCES properties(id) ON DELETE CASCADE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (tenant_id, property_id)
    );

CREATE INDEX idx_shortlist_tenant   ON tenant_shortlists (tenant_id);
CREATE INDEX idx_shortlist_property ON tenant_shortlists (property_id);