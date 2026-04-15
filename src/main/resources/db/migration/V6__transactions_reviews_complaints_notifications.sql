-- ═══════════════════════════════════════════════════════════════
-- RentEase — V6: Transactions, Reviews, Complaints, Notifications
-- ═══════════════════════════════════════════════════════════════

-- ── Transactions ──────────────────────────────────────────────────────────────
-- Immutable financial ledger. Never UPDATE rows — create new ones for refunds.

CREATE TABLE IF NOT EXISTS transactions (
                                            id                    UUID          PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id               UUID          NOT NULL REFERENCES users(id),
    type                  VARCHAR(30)   NOT NULL
    CHECK (type IN ('SUBSCRIPTION','BOOST','COMMISSION',
           'SECURITY_DEPOSIT','RENT','REFUND')),
    amount                DECIMAL(10,2) NOT NULL,
    currency              VARCHAR(5)    NOT NULL DEFAULT 'BDT',
    gateway               VARCHAR(20)   NOT NULL
    CHECK (gateway IN ('BKASH','NAGAD','SSLCOMMERZ','INTERNAL')),
    gateway_reference     VARCHAR(300),
    status                VARCHAR(20)   NOT NULL DEFAULT 'PENDING'
    CHECK (status IN ('PENDING','SUCCESS','FAILED','REFUNDED')),
    parent_transaction_id UUID          REFERENCES transactions(id),
    metadata              JSONB,
    description           VARCHAR(500),

    -- Transactions are immutable — no updated_at
    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by   UUID,

    CONSTRAINT fk_txn_user FOREIGN KEY (user_id) REFERENCES users(id)
    );

CREATE INDEX idx_txn_user    ON transactions (user_id);
CREATE INDEX idx_txn_type    ON transactions (type);
CREATE INDEX idx_txn_status  ON transactions (status);
CREATE INDEX idx_txn_gateway ON transactions (gateway);
CREATE INDEX idx_txn_ref     ON transactions (gateway_reference);
CREATE INDEX idx_txn_date    ON transactions (created_at DESC);

-- ── Reviews ───────────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS reviews (
                                       id              UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    reviewer_id     UUID        NOT NULL REFERENCES users(id),
    target_id       UUID        NOT NULL REFERENCES users(id),
    property_id     UUID        REFERENCES properties(id),
    lease_id        UUID        NOT NULL REFERENCES leases(id),
    -- 1 to 5 stars only
    rating          SMALLINT    NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment         TEXT,
    is_published    BOOLEAN     NOT NULL DEFAULT TRUE,
    moderation_note TEXT,

    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ,
    created_by   UUID,
    updated_by   UUID,
    deleted_at   TIMESTAMPTZ,

    CONSTRAINT fk_review_reviewer FOREIGN KEY (reviewer_id) REFERENCES users(id),
    CONSTRAINT fk_review_target   FOREIGN KEY (target_id)   REFERENCES users(id),
    CONSTRAINT fk_review_property FOREIGN KEY (property_id) REFERENCES properties(id),
    CONSTRAINT fk_review_lease    FOREIGN KEY (lease_id)    REFERENCES leases(id),

    -- One review per reviewer per lease
    CONSTRAINT uq_review_reviewer_lease UNIQUE (reviewer_id, lease_id)
    );

CREATE INDEX idx_review_reviewer  ON reviews (reviewer_id);
CREATE INDEX idx_review_target    ON reviews (target_id);
CREATE INDEX idx_review_property  ON reviews (property_id);
CREATE INDEX idx_review_published ON reviews (is_published) WHERE is_published = TRUE;

CREATE TRIGGER trg_reviews_updated_at
    BEFORE UPDATE ON reviews
    FOR EACH ROW EXECUTE FUNCTION trigger_set_updated_at();

-- ── Complaints ────────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS complaints (
                                          id               UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    filed_by_id      UUID        NOT NULL REFERENCES users(id),
    against_id       UUID        REFERENCES users(id),
    property_id      UUID        REFERENCES properties(id),
    complaint_type   VARCHAR(30) NOT NULL
    CHECK (complaint_type IN ('FRAUD','HARASSMENT','PROPERTY_ISSUE',
           'PAYMENT_DISPUTE','FAKE_LISTING','OTHER')),
    description      TEXT        NOT NULL,
    status           VARCHAR(20) NOT NULL DEFAULT 'OPEN'
    CHECK (status IN ('OPEN','UNDER_REVIEW','RESOLVED','DISMISSED')),
    admin_notes      TEXT,
    resolved_by_id   UUID,
    resolved_at      TIMESTAMPTZ,

    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ,
    created_by   UUID,
    updated_by   UUID,
    deleted_at   TIMESTAMPTZ,

    CONSTRAINT fk_complaint_filed_by FOREIGN KEY (filed_by_id) REFERENCES users(id),
    CONSTRAINT fk_complaint_against  FOREIGN KEY (against_id)  REFERENCES users(id),
    CONSTRAINT fk_complaint_property FOREIGN KEY (property_id) REFERENCES properties(id)
    );

CREATE TABLE IF NOT EXISTS complaint_evidence (
                                                  complaint_id UUID NOT NULL REFERENCES complaints(id) ON DELETE CASCADE,
    evidence_url TEXT NOT NULL,
    PRIMARY KEY (complaint_id, evidence_url)
    );

CREATE INDEX idx_complaint_filed_by ON complaints (filed_by_id);
CREATE INDEX idx_complaint_against  ON complaints (against_id);
CREATE INDEX idx_complaint_status   ON complaints (status);

CREATE TRIGGER trg_complaints_updated_at
    BEFORE UPDATE ON complaints
    FOR EACH ROW EXECUTE FUNCTION trigger_set_updated_at();

-- ── Notifications ─────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS notifications (
                                             id           UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id      UUID        NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title        VARCHAR(200) NOT NULL,
    body         TEXT         NOT NULL,
    type         VARCHAR(40)  NOT NULL
    CHECK (type IN ('LISTING_APPROVED','LISTING_REJECTED','NEW_INQUIRY',
           'INQUIRY_ACCEPTED','INQUIRY_REJECTED','NEW_MESSAGE',
           'LEASE_SENT','LEASE_SIGNED','RENT_DUE','RENT_OVERDUE',
           'SUBSCRIPTION_EXPIRING','SUBSCRIPTION_EXPIRED',
           'KYC_APPROVED','KYC_REJECTED','SYSTEM','PROMOTION')),
    is_read      BOOLEAN      NOT NULL DEFAULT FALSE,
    reference_id UUID,
    deep_link    VARCHAR(300),

    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    -- Notifications are append-only; no updated_at needed
    created_by   UUID,

    CONSTRAINT fk_notif_user FOREIGN KEY (user_id) REFERENCES users(id)
    );

CREATE INDEX idx_notif_user    ON notifications (user_id, created_at DESC);
CREATE INDEX idx_notif_unread  ON notifications (user_id, is_read) WHERE is_read = FALSE;
CREATE INDEX idx_notif_type    ON notifications (type);

-- Auto-purge notifications older than 90 days (run via pg_cron or Spring scheduler)
-- CREATE INDEX idx_notif_cleanup ON notifications (created_at) WHERE created_at < NOW() - INTERVAL '90 days';

-- ── Audit Log ─────────────────────────────────────────────────────────────────
-- Every admin action is recorded here for compliance and debugging.

CREATE TABLE IF NOT EXISTS audit_logs (
                                          id           UUID         PRIMARY KEY DEFAULT uuid_generate_v4(),
    actor_id     UUID         NOT NULL REFERENCES users(id),
    action       VARCHAR(100) NOT NULL,  -- e.g. 'LISTING_APPROVE', 'USER_SUSPEND'
    target_table VARCHAR(50),
    target_id    UUID,
    old_value    JSONB,
    new_value    JSONB,
    ip_address   VARCHAR(45),
    user_agent   TEXT,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT NOW()
    );

CREATE INDEX idx_audit_actor    ON audit_logs (actor_id);
CREATE INDEX idx_audit_action   ON audit_logs (action);
CREATE INDEX idx_audit_target   ON audit_logs (target_table, target_id);
CREATE INDEX idx_audit_date     ON audit_logs (created_at DESC);