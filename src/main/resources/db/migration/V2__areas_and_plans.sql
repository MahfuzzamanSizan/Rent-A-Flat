-- ═══════════════════════════════════════════════════════════════
-- RentEase — V2: Areas & Subscription Plans
-- ═══════════════════════════════════════════════════════════════

-- ── Areas ─────────────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS areas (
                                     id           UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    city         VARCHAR(100) NOT NULL,
    district     VARCHAR(100) NOT NULL,
    area_name    VARCHAR(150) NOT NULL,
    sub_area     VARCHAR(150),
    latitude     DECIMAL(10,8),
    longitude    DECIMAL(11,8),
    is_active    BOOLEAN      NOT NULL DEFAULT TRUE,

    -- Audit columns (populated by JPA AuditingEntityListener)
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ,
    created_by   UUID,
    updated_by   UUID,
    deleted_at   TIMESTAMPTZ
    );

CREATE INDEX idx_areas_city     ON areas (city);
CREATE INDEX idx_areas_district ON areas (district);
CREATE INDEX idx_areas_active   ON areas (is_active) WHERE is_active = TRUE;

-- Trigger: keep updated_at fresh
CREATE TRIGGER trg_areas_updated_at
    BEFORE UPDATE ON areas
    FOR EACH ROW EXECUTE FUNCTION trigger_set_updated_at();

-- ── Subscription Plans ────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS subscription_plans (
                                                  id              UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    name            VARCHAR(100) NOT NULL,
    target_role     VARCHAR(20)  NOT NULL
    CHECK (target_role IN ('OWNER', 'TENANT')),
    price           DECIMAL(10,2) NOT NULL,
    duration_days   INT          NOT NULL,
    max_listings    INT          NOT NULL DEFAULT 1,  -- -1 = unlimited
    max_photos      INT          NOT NULL DEFAULT 3,
    boost_credits   INT          NOT NULL DEFAULT 0,
    max_contacts    INT          NOT NULL DEFAULT 3,  -- -1 = unlimited
    features        JSONB,
    is_active       BOOLEAN      NOT NULL DEFAULT TRUE,

    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ,
    created_by   UUID,
    updated_by   UUID,
    deleted_at   TIMESTAMPTZ
    );

CREATE INDEX idx_plans_role   ON subscription_plans (target_role);
CREATE INDEX idx_plans_active ON subscription_plans (is_active) WHERE is_active = TRUE;

CREATE TRIGGER trg_plans_updated_at
    BEFORE UPDATE ON subscription_plans
    FOR EACH ROW EXECUTE FUNCTION trigger_set_updated_at();

-- ── Seed: default subscription plans ─────────────────────────────────────────

INSERT INTO subscription_plans
(id, name, target_role, price, duration_days, max_listings, max_photos,
 boost_credits, max_contacts, features, is_active)
VALUES
    -- Owner Plans
    (uuid_generate_v4(), 'Basic (Free)', 'OWNER', 0.00, 30,
     1, 3, 0, -1,
     '{"digitalLease": false, "rentTracking": false, "analytics": "basic", "verifiedBadge": false, "prioritySupport": false}',
     TRUE),

    (uuid_generate_v4(), 'Pro', 'OWNER', 299.00, 30,
     5, 10, 1, -1,
     '{"digitalLease": true, "rentTracking": true, "analytics": "standard", "verifiedBadge": false, "prioritySupport": false}',
     TRUE),

    (uuid_generate_v4(), 'Premium', 'OWNER', 699.00, 30,
     -1, -1, 3, -1,
     '{"digitalLease": true, "rentTracking": true, "analytics": "full", "verifiedBadge": true, "prioritySupport": true}',
     TRUE),

    -- Tenant Plans
    (uuid_generate_v4(), 'Free', 'TENANT', 0.00, 30,
     0, 0, 0, 3,
     '{"savedSearches": 1, "areaAlerts": false, "mapView": "limited", "chatAccess": false, "verifiedBadge": false}',
     TRUE),

    (uuid_generate_v4(), 'Basic', 'TENANT', 99.00, 30,
     0, 0, 0, 20,
     '{"savedSearches": 5, "areaAlerts": true, "mapView": "full", "chatAccess": true, "verifiedBadge": false}',
     TRUE),

    (uuid_generate_v4(), 'Premium', 'TENANT', 199.00, 30,
     0, 0, 0, -1,
     '{"savedSearches": -1, "areaAlerts": true, "mapView": "full", "chatAccess": true, "verifiedBadge": true}',
     TRUE);

-- ── Seed: Dhaka areas ─────────────────────────────────────────────────────────

INSERT INTO areas (id, city, district, area_name, sub_area, latitude, longitude, is_active)
VALUES
    (uuid_generate_v4(), 'Dhaka', 'Dhaka',   'Mirpur',        'Mirpur-1',       23.8041, 90.3654, TRUE),
    (uuid_generate_v4(), 'Dhaka', 'Dhaka',   'Mirpur',        'Mirpur-10',      23.8223, 90.3654, TRUE),
    (uuid_generate_v4(), 'Dhaka', 'Dhaka',   'Mirpur',        'Mirpur-12',      23.8300, 90.3700, TRUE),
    (uuid_generate_v4(), 'Dhaka', 'Dhaka',   'Dhanmondi',     'Dhanmondi-27',   23.7461, 90.3742, TRUE),
    (uuid_generate_v4(), 'Dhaka', 'Dhaka',   'Dhanmondi',     'Dhanmondi-15',   23.7500, 90.3720, TRUE),
    (uuid_generate_v4(), 'Dhaka', 'Dhaka',   'Uttara',        'Uttara Sector 3',23.8759, 90.3795, TRUE),
    (uuid_generate_v4(), 'Dhaka', 'Dhaka',   'Uttara',        'Uttara Sector 7',23.8700, 90.3800, TRUE),
    (uuid_generate_v4(), 'Dhaka', 'Dhaka',   'Mohammadpur',   'Mohammadpur',    23.7659, 90.3564, TRUE),
    (uuid_generate_v4(), 'Dhaka', 'Dhaka',   'Bashundhara',   'Block A',        23.8228, 90.4265, TRUE),
    (uuid_generate_v4(), 'Dhaka', 'Dhaka',   'Bashundhara',   'Block C',        23.8200, 90.4280, TRUE),
    (uuid_generate_v4(), 'Dhaka', 'Dhaka',   'Gulshan',       'Gulshan-1',      23.7808, 90.4142, TRUE),
    (uuid_generate_v4(), 'Dhaka', 'Dhaka',   'Gulshan',       'Gulshan-2',      23.7937, 90.4142, TRUE),
    (uuid_generate_v4(), 'Dhaka', 'Dhaka',   'Banani',        'Banani',         23.7936, 90.4066, TRUE),
    (uuid_generate_v4(), 'Dhaka', 'Dhaka',   'Rampura',       'Rampura',        23.7600, 90.4270, TRUE),
    (uuid_generate_v4(), 'Dhaka', 'Dhaka',   'Khilgaon',      'Khilgaon',       23.7430, 90.4280, TRUE),
    (uuid_generate_v4(), 'Dhaka', 'Dhaka',   'Badda',         'Badda',          23.7800, 90.4320, TRUE),
    (uuid_generate_v4(), 'Dhaka', 'Dhaka',   'Shyamoli',      'Shyamoli',       23.7730, 90.3630, TRUE),
    (uuid_generate_v4(), 'Dhaka', 'Dhaka',   'Farmgate',      'Farmgate',       23.7563, 90.3870, TRUE),
    (uuid_generate_v4(), 'Dhaka', 'Dhaka',   'Malibagh',      'Malibagh',       23.7480, 90.4180, TRUE),
    (uuid_generate_v4(), 'Dhaka', 'Dhaka',   'Wari',          'Wari',           23.7220, 90.4120, TRUE);