package com.bkuberek.bookings.db.entities;

import com.bkuberek.bookings.db.Endorsement;
import java.util.UUID;

public record RestaurantEndorsementEntity(UUID restaurantId, Endorsement endorsement) {}
