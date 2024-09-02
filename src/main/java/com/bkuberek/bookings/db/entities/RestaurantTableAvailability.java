package com.bkuberek.bookings.db.entities;

import com.bkuberek.bookings.db.Endorsement;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public record RestaurantTableAvailability(
    UUID id,
    String name,
    Integer size,
    HashSet<Endorsement> endorsements,
    List<RestaurantTableEntity> tables,
    List<RestaurantTableEntity> occupiedTables,
    List<RestaurantTableEntity> availableTables) {
  public RestaurantTableAvailability(UUID id, String name, Integer size) {
    this(
        id,
        name,
        size,
        Sets.newHashSet(),
        Lists.newArrayList(),
        Lists.newArrayList(),
        Lists.newArrayList());
  }
}
