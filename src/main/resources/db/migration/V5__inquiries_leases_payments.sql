-- ═══════════════════════════════════════════════════════════════
-- RentEase — V5: Inquiries, Leases & Rent Payments
-- ═══════════════════════════════════════════════════════════════

-- ── Inquiries ─────────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS inquiries (
                                         id                      UUID        PRIMARY KEY DEFAULT uuid_generate_v4(),
    tenant_id               UUID        NOT NULL REFERENCES users(id),
    property_id             UUID        NOT NULL REFERENCES properties(id),
    owner_id                UUID        NOT NULL REFERENCES users(id),
    message                 TEXT,
    status                  VARCHAR(20) NOT NULL DEFAULT 'PENDING'
    CHECK (status IN ('PENDING','ACCEPTED','REJECTED','WITHDRAWN')),
    owner_response_message  TEXT,
    responded_at            TIMESTAMPTZ,
    -- Chat thread identifier opened on ACCEPTED status
    chat_thread_id          VARCHAR(200),

    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ,
    created_by   UUID,
    updated_by   UUID,
    deleted_at   TIMESTAMPTZ,

    CONSTRAINT fk_inquiry_tenant   FOREIGN KEY (tenant_id)   REFERENCES users(id),
    CONSTRAINT fk_inquiry_property FOREIGN KEY (property_id) REFERENCES properties(id),
    CONSTRAINT fk_inquiry_owner    FOREIGN KEY (owner_id)    REFERENCES users(id),

    -- One active inquiry per tenant per property
    CONSTRAINT uq_inquiry_tenant_property UNIQUE (tenant_id, property_id)
    );

CREATE INDEX idx_inquiry_tenant   ON inquiries (tenant_id);
CREATE INDEX idx_inquiry_owner    ON inquiries (owner_id);
CREATE INDEX idx_inquiry_property ON inquiries (property_id);
CREATE INDEX idx_inquiry_status   ON inquiries (status);

CREATE TRIGGER trg_inquiries_updated_at
    BEFORE UPDATE ON inquiries
    FOR EACH ROW EXECUTE FUNCTION trigger_set_updated_at();

-- ── Leases ────────────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS leases (
                                      id                  UUID          PRIMARY KEY DEFAULT uuid_generate_v4(),
    property_id         UUID          NOT NULL REFERENCES properties(id),
    tenant_id           UUID          NOT NULL REFERENCES users(id),
    owner_id            UUID          NOT NULL REFERENCES users(id),
    start_date          DATE          NOT NULL,
    end_date            DATE          NOT NULL,
    rent_amount         DECIMAL(10,2) NOT NULL,
    security_deposit    DECIMAL(10,2),
    -- Day of month rent is due (1–28 to avoid month-end edge cases)
    rent_due_day        SMALLINT      NOT NULL DEFAULT 5
    CHECK (rent_due_day BETWEEN 1 AND 28),
    notice_period_days  INT           NOT NULL DEFAULT 30,
    terms               TEXT,
    status              VARCHAR(30)   NOT NULL DEFAULT 'DRAFT'
    CHECK (status IN ('DRAFT','PENDING_SIGNATURE','ACTIVE','TERMINATED','EXPIRED')),
    owner_signed_at     TIMESTAMPTZ,
    tenant_signed_at    TIMESTAMPTZ,
    termination_reason  TEXT,
    terminated_by_id    UUID,
    terminated_at       TIMESTAMPTZ,
    lease_document_url  TEXT,

    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ,
    created_by   UUID,
    updated_by   UUID,
    deleted_at   TIMESTAMPTZ,

    CONSTRAINT fk_lease_property FOREIGN KEY (property_id) REFERENCES properties(id),
    CONSTRAINT fk_lease_tenant   FOREIGN KEY (tenant_id)   REFERENCES users(id),
    CONSTRAINT fk_lease_owner    FOREIGN KEY (owner_id)    REFERENCES users(id),

    -- Validate date range
    CONSTRAINT chk_lease_dates CHECK (end_date > start_date)
    );

CREATE INDEX idx_lease_property ON leases (property_id);
CREATE INDEX idx_lease_tenant   ON leases (tenant_id);
CREATE INDEX idx_lease_owner    ON leases (owner_id);
CREATE INDEX idx_lease_status   ON leases (status);

-- Partial index: quickly find ACTIVE leases per property
CREATE UNIQUE INDEX idx_lease_active_property
    ON leases (property_id)
    WHERE status = 'ACTIVE';

CREATE TRIGGER trg_leases_updated_at
    BEFORE UPDATE ON leases
    FOR EACH ROW EXECUTE FUNCTION trigger_set_updated_at();

-- ── Rent Payments ─────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS rent_payments (
                                             id                    UUID          PRIMARY KEY DEFAULT uuid_generate_v4(),
    lease_id              UUID          NOT NULL REFERENCES leases(id) ON DELETE CASCADE,
    amount                DECIMAL(10,2) NOT NULL,
    due_date              DATE          NOT NULL,
    paid_at               TIMESTAMPTZ,
    payment_method        VARCHAR(20)
    CHECK (payment_method IN ('BKASH','NAGAD','CARD','CASH','BANK_TRANSFER')),
    transaction_reference VARCHAR(200),
    status                VARCHAR(20)   NOT NULL DEFAULT 'PENDING'
    CHECK (status IN ('PENDING','PAID','OVERDUE','WAIVED')),
    receipt_url           TEXT,
    notes                 TEXT,

    created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMPTZ,
    created_by   UUID,
    updated_by   UUID,
    deleted_at   TIMESTAMPTZ,

    CONSTRAINT fk_rentpay_lease FOREIGN KEY (lease_id) REFERENCES leases(id)
    );

CREATE INDEX idx_rentpay_lease   ON rent_payments (lease_id);
CREATE INDEX idx_rentpay_status  ON rent_payments (status);
CREATE INDEX idx_rentpay_due     ON rent_payments (due_date);

-- Find overdue payments quickly (scheduled job)
CREATE INDEX idx_rentpay_overdue ON rent_payments (due_date, status)
    WHERE status = 'PENDING';

CREATE TRIGGER trg_rentpayments_updated_at
    BEFORE UPDATE ON rent_payments
    FOR EACH ROW EXECUTE FUNCTION trigger_set_updated_at();