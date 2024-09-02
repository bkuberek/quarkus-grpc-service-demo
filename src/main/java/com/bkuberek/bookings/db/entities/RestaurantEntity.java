package com.bkuberek.bookings.db.entities;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public record RestaurantEntity(
    UUID id,
    String name,
    ZonedDateTime createdTime,
    ZonedDateTime updatedTime,
    HashSet<RestaurantEndorsementEntity> endorsements,
    List<RestaurantTableEntity> tables) {
  public RestaurantEntity(
      UUID id, String name, ZonedDateTime createdTime, ZonedDateTime updatedTime) {
    this(id, name, createdTime, updatedTime, Sets.newHashSet(), Lists.newArrayList());
  }
}
