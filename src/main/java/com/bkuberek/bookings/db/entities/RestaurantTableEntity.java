package com.bkuberek.bookings.db.entities;

import java.util.UUID;

public record RestaurantTableEntity(UUID restaurantId, Integer size, Integer quantity) {}
