package com.bkuberek.bookings.services;

import com.bkuberek.bookings.db.entities.RestaurantEntity;
import com.bkuberek.bookings.db.entities.RestaurantTableAvailability;
import com.bkuberek.bookings.db.repositories.RestaurantRepository;
import com.bkuberek.bookings.grpc.*;
import com.bkuberek.bookings.grpc.services.*;
import com.google.protobuf.Timestamp;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@GrpcService
public class RestaurantService implements RestaurantGrpc {

  private final RestaurantRepository restaurantRepository;

  @Inject
  public RestaurantService(RestaurantRepository restaurantRepository) {
    this.restaurantRepository = restaurantRepository;
  }

  @Override
  public Uni<RestaurantInfoList> allRestaurants(EmptyRequest request) {
    return restaurantRepository.listAll().onItem().transform(restaurantInfoListTransformFunction());
  }

  @Override
  public Uni<RestaurantInfo> getRestaurantById(GetRestaurantByIdRequest request) {
    return restaurantRepository
        .getById(UUID.fromString(request.getId()))
        .onItem()
        .transform(restaurantInfoTransformFunction());
  }

  @Override
  public Uni<RestaurantInfoList> getRestaurantsById(GetRestaurantsByIdRequest request) {
    return restaurantRepository
        .getByIds(request.getIdsList().stream().map(UUID::fromString).toList())
        .onItem()
        .transform(restaurantInfoListTransformFunction());
  }

  @Override
  public Uni<RestaurantInfoList> getRestaurantsByName(GetRestaurantsByNameRequest request) {
    return restaurantRepository
        .findByName(request.getNamesList())
        .onItem()
        .transform(restaurantInfoListTransformFunction());
  }

  @Override
  public Uni<RestaurantInfoList> findRestaurantsWithEndorsements(
      FindRestaurantsWithEndorsementsRequest request) {
    return restaurantRepository
        .findRestaurantsByEndorsement(
            request.getEndorsementsList().stream()
                .map(e -> com.bkuberek.bookings.db.Endorsement.valueOf(e.toString()))
                .toList())
        .onItem()
        .transform(restaurantInfoListTransformFunction());
  }

  @Override
  public Uni<RestaurantTablesAvailable> getAvailableTables(GetAvailableTablesRequest request) {
    Timestamp time = request.getTime();
    return restaurantRepository
        .getAvailableTables(
            UUID.fromString(request.getRestaurantId()),
            request.getSize(),
            Instant.ofEpochSecond(time.getSeconds(), time.getNanos())
                .atZone(ZoneId.systemDefault()))
        .onItem()
        .transform(restaurantTableAvailabilityTransformFunction());
  }

  @Override
  public Uni<FindTableResponse> findTable(FindTableRequest request) {
    Timestamp time = request.getTime();
    return restaurantRepository
        .findRestaurantsWithAvailableTableAndRestrictions(
            request.getSize(),
            Instant.ofEpochSecond(time.getSeconds(), time.getNanos())
                .atZone(ZoneId.systemDefault()),
            request.getEndorsementsList().stream()
                .map(e -> com.bkuberek.bookings.db.Endorsement.valueOf(e.toString()))
                .toList())
        .onItem()
        .transform(
            items ->
                FindTableResponse.newBuilder()
                    .addAllAvailable(
                        items.stream().map(restaurantTableAvailabilityTransformFunction()).toList())
                    .build());
  }

  public static Function<List<RestaurantEntity>, RestaurantInfoList>
      restaurantInfoListTransformFunction() {
    return restaurantEntities ->
        RestaurantInfoList.newBuilder()
            .addAllRestaurants(
                restaurantEntities.stream()
                    .map(restaurantInfoTransformFunction())
                    .collect(Collectors.toSet()))
            .build();
  }

  public static Function<RestaurantEntity, RestaurantInfo> restaurantInfoTransformFunction() {
    return restaurantEntity -> {
      List<Endorsement> endorsements =
          restaurantEntity.endorsements().stream()
              .map(e -> Endorsement.valueOf(e.endorsement().toString()))
              .toList();

      List<TableInfo> tables =
          restaurantEntity.tables().stream()
              .map(t -> TableInfo.newBuilder().setSize(t.size()).setQuantity(t.quantity()).build())
              .toList();

      return RestaurantInfo.newBuilder()
          .setId(restaurantEntity.id().toString())
          .setName(restaurantEntity.name())
          .addAllEndorsements(endorsements)
          .addAllTables(tables)
          .build();
    };
  }

  public static Function<RestaurantTableAvailability, RestaurantTablesAvailable>
      restaurantTableAvailabilityTransformFunction() {
    return availability -> {
      List<Endorsement> endorsements =
          availability.endorsements().stream().map(e -> Endorsement.valueOf(e.toString())).toList();
      List<TableInfo> tables =
          availability.tables().stream()
              .map(t -> TableInfo.newBuilder().setSize(t.size()).setQuantity(t.quantity()).build())
              .toList();
      List<TableInfo> occupiedTables =
          availability.occupiedTables().stream()
              .map(t -> TableInfo.newBuilder().setSize(t.size()).setQuantity(t.quantity()).build())
              .toList();
      List<TableInfo> availableTables =
          availability.availableTables().stream()
              .map(t -> TableInfo.newBuilder().setSize(t.size()).setQuantity(t.quantity()).build())
              .toList();

      return RestaurantTablesAvailable.newBuilder()
          .setId(availability.id().toString())
          .setName(availability.name())
          .setSize(availability.size())
          .addAllEndorsements(endorsements)
          .addAllTables(tables)
          .addAllOccupiedTables(occupiedTables)
          .addAllAvailableTables(availableTables)
          .build();
    };
  }
}
