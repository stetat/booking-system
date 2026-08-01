CREATE TABLE outbox(
    id BIGSERIAL PRIMARY KEY,
    aggregate_id UUID NOT NULL,
    topic TEXT NOT NULL,
    payload TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    published_at TIMESTAMPTZ
);

CREATE INDEX idx_outbox_unpublished
ON outbox(id)
WHERE published_at IS NULL;