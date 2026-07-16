CREATE TABLE event (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    name text NOT NULL,
    venue text NOT NULL,
    starts_at timestamptz NOT NULL
);

CREATE TABLE seat (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    event_id uuid NOT NULL REFERENCES event (id),
    label text NOT NULL,
    UNIQUE (event_id, label)
);

CREATE TABLE booking (
    id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id text NOT NULL,
    seat_id uuid NOT NULL REFERENCES seat (id),
    status text NOT NULL CHECK (status in ('PENDING', 'CONFIRMED', 'CANCELLED')),
    created_at timestamptz NOT NULL DEFAULT now()

);

CREATE UNIQUE INDEX uq_booking_one_active_per_seat
       ON booking (seat_id)
       WHERE status <> 'CANCELLED';
