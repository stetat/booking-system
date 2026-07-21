CREATE UNIQUE INDEX uq_booking_one_active_per_seat
ON booking (seat_id)
WHERE status <> 'CANCELLED';