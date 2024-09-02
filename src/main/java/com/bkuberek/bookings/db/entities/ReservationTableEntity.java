package com.bkuberek.bookings.db.entities;

import java.util.UUID;

public record ReservationTableEntity(UUID reservationId, Integer size, Integer quantity) {}
