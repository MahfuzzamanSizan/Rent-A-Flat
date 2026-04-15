-- ═══════════════════════════════════════════════════════════════
-- RentEase — V3: Users & Subscriptions
-- ═══════════════════════════════════════════════════════════════

-- ── Users ─────────────────────────────────────────────────────────────────────
-- Forward-declare without the FK to subscriptions (circular ref resolved below)

CREATE TABLE IF NOT EXISTS users (
                                     id                     UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    phone                  VARCHAR(20)  NOT NULL UNIQUE,
    email                  VARCHAR(150),
    full_name              VARCHAR(100) NOT NULL,
    role                   VARCHAR(20)  NOT NULL
    CHECK (role IN ('TENANT', 'OWNER', 'ADMIN')),
    kyc_status             VARCHAR(20)  NOT NULL DEFAULT 'NOT_SUBMITTED'
    CHECK (kyc_status IN ('NOT_SUBMITTED','PENDING','VERIFIED','REJECTED')),
    nid_front_url          TEXT,
    nid_back_url           TEXT,
    profile_photo_url      TEXT,
    occupation             VARCHAR(100),
    income_range           VARCHAR(50),
    is_active              BOOLEAN      NOT NULL DEFAULT TRUE,
    kyc_rejection_reason   TEXT,
    fcm_token              VARCHAR(500),
    average_rating         DECIMAL(3,2) NOT NULL DEFAULT 0.00,
    total_reviews          INT          NOT NULL DEFAULT 0,
    active_subscription_id UUID,        -- FK added after subscriptions table is created

    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ,
    created_by   UUID,
    updated_by   UUID,
    deleted_at   TIMESTAMPTZ
    );

CREATE UNIQUE INDEX idx_users_phone      ON users (phone);
CREATE INDEX        idx_users_role       ON users (role);
CREATE INDEX        idx_users_kyc        ON users (kyc_status);
CREATE INDEX        idx_users_active     ON users (is_active) WHERE is_active = TRUE;
CREATE INDEX        idx_users_deleted    ON users (deleted_at) WHERE deleted_at IS NULL;

-- GIN index for trigram search on full_name (admin user search)
CREATE INDEX idx_users_name_trgm ON users USING GIN (full_name gin_trgm_ops);

CREATE TRIGGER trg_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION trigger_set_updated_at();

-- ── Subscriptions ─────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS subscriptions (
                                             id                      UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id                 UUID        NOT NULL
    REFERENCES users(id) ON DELETE CASCADE,
    plan_id                 UUID        NOT NULL
    REFERENCES subscription_plans(id),
    status                  VARCHAR(20) NOT NULL DEFAULT 'PENDING_PAYMENT'
    CHECK (status IN ('ACTIVE','EXPIRED','CANCELLED','PENDING_PAYMENT')),
    start_date              TIMESTAMPTZ,
    end_date                TIMESTAMPTZ,
    auto_renew              BOOLEAN     NOT NULL DEFAULT TRUE,
    payment_reference       VARCHAR(200),
    boost_credits_remaining INT         NOT NULL DEFAULT 0,
    contacts_remaining      INT,

    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ,
    created_by   UUID,
    updated_by   UUID,
    deleted_at   TIMESTAMPTZ,

    CONSTRAINT fk_subscription_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_subscription_plan FOREIGN KEY (plan_id) REFERENCES subscription_plans(id)
    );

CREATE INDEX idx_sub_user   ON subscriptions (user_id);
CREATE INDEX idx_sub_status ON subscriptions (status);
CREATE INDEX idx_sub_expiry ON subscriptions (end_date);

CREATE TRIGGER trg_subscriptions_updated_at
    BEFORE UPDATE ON subscriptions
    FOR EACH ROW EXECUTE FUNCTION trigger_set_updated_at();

-- ── Resolve circular FK: users.active_subscription_id → subscriptions ─────────

ALTER TABLE users
    ADD CONSTRAINT fk_users_subscription
        FOREIGN KEY (active_subscription_id)
            REFERENCES subscriptions(id)
            ON DELETE SET NULL
            DEFERRABLE INITIALLY DEFERRED;

-- ── Seed: default admin user ──────────────────────────────────────────────────

INSERT INTO users (id, phone, full_name, role, kyc_status, is_active)
VALUES (
           uuid_generate_v4(),
           '+8801719667855',
           'System Admin',
           'ADMIN',
           'VERIFIED',
           TRUE
       );