CREATE TABLE IF NOT EXISTS chat_messages (
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    inquiry_id  UUID        NOT NULL REFERENCES inquiries(id),
    sender_id   UUID        NOT NULL REFERENCES users(id),
    content     TEXT        NOT NULL,
    is_read     BOOLEAN     NOT NULL DEFAULT false,
    sent_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_chat_inquiry ON chat_messages(inquiry_id);
CREATE INDEX IF NOT EXISTS idx_chat_sender  ON chat_messages(sender_id);
CREATE INDEX IF NOT EXISTS idx_chat_unread  ON chat_messages(inquiry_id, is_read) WHERE is_read = false;
