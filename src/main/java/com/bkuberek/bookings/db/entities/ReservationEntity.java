package com.bkuberek.bookings.db.entities;

import com.bkuberek.bookings.db.Endorsement;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record ReservationEntity(
    UUID id,
    String name,
    Integer size,
    Boolean isActive,
    RestaurantEntity restaurant,
    ZonedDateTime reservationTime,
    ZonedDateTime createdTime,
    ZonedDateTime updatedTime,
    Set<Endorsement> restrictions,
    List<ReservationTableEntity> tables) {
  public ReservationEntity(
      UUID id,
      String name,
      Integer size,
      Boolean isActive,
      RestaurantEntity restaurant,
      ZonedDateTime reservationTime,
      ZonedDateTime createdTime,
      ZonedDateTime updatedTime,
      Set<Endorsement> restrictions) {
    this(
        id,
        name,
        size,
        isActive,
        restaurant,
        reservationTime,
        createdTime,
        updatedTime,
        restrictions,
        Collections.emptyList());
  }

  public ReservationEntity(
      UUID id,
      String name,
      Integer size,
      Boolean isActive,
      RestaurantEntity restaurant,
      ZonedDateTime reservationTime,
      ZonedDateTime createdTime,
      ZonedDateTime updatedTime) {
    this(
        id,
        name,
        size,
        isActive,
        restaurant,
        reservationTime,
        createdTime,
        updatedTime,
        Collections.emptySet(),
        Collections.emptyList());
  }

  public ReservationEntity withRestaurant(RestaurantEntity restaurant) {
    return new ReservationEntity(
        id,
        name,
        size,
        isActive,
        restaurant,
        reservationTime,
        createdTime,
        updatedTime,
        restrictions,
        tables);
  }
}
