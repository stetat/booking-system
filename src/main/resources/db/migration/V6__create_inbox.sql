CREATE TABLE inbox (
    id BIGSERIAL PRIMARY KEY,
    consumer_group TEXT NOT NULL,
    message_id UUID NOT NULL,
    processed_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT uq_msg_id_per_group
                   UNIQUE (consumer_group, message_id)
);