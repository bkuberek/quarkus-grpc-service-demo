package com.bkuberek.bookings.services;

import static com.bkuberek.bookings.services.RestaurantService.restaurantInfoTransformFunction;

import com.bkuberek.bookings.db.entities.ReservationEntity;
import com.bkuberek.bookings.db.repositories.ReservationRepository;
import com.bkuberek.bookings.db.repositories.RestaurantRepository;
import com.bkuberek.bookings.grpc.Endorsement;
import com.bkuberek.bookings.grpc.ReservationInfo;
import com.bkuberek.bookings.grpc.ReservationInfoList;
import com.bkuberek.bookings.grpc.TableInfo;
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
public class ReservationService implements ReservationGrpc {

  private final ReservationRepository reservationRepository;
  private final RestaurantRepository restaurantRepository;

  @Inject
  public ReservationService(
      ReservationRepository reservationRepository, RestaurantRepository restaurantRepository) {
    this.reservationRepository = reservationRepository;
    this.restaurantRepository = restaurantRepository;
  }

  @Override
  public Uni<ReservationInfo> getReservation(GetReservationRequest request) {
    return reservationRepository
        .getById(UUID.fromString(request.getId()))
        .onItem()
        .transform(reservationInfoTransformFunction());
  }

  @Override
  public Uni<ReservationInfoList> getReservations(GetReservationsRequest request) {
    return reservationRepository
        .listAllForUser(request.getName())
        .onItem()
        .transform(
            reservationEntities ->
                ReservationInfoList.newBuilder()
                    .addAllReservations(
                        reservationEntities.stream()
                            .map(reservationInfoTransformFunction())
                            .toList())
                    .build());
  }

  @Override
  public Uni<CreateReservationResponse> createReservation(CreateReservationRequest request) {
    Timestamp reservationTime = request.getReservationTime();
    // fixme: validation
    return restaurantRepository
        .getById(UUID.fromString(request.getRestaurantId()))
        .flatMap(
            restaurant -> {
              ReservationEntity entity =
                  new ReservationEntity(
                      UUID.randomUUID(),
                      request.getName(),
                      request.getSize(),
                      true,
                      restaurant,
                      Instant.ofEpochSecond(
                              reservationTime.getSeconds(), reservationTime.getNanos())
                          .atZone(ZoneId.systemDefault()),
                      request.getRestrictionsList().stream()
                          .map(e -> com.bkuberek.bookings.db.Endorsement.valueOf(e.toString()))
                          .collect(Collectors.toSet()));
              return reservationRepository
                  .createReservation(entity)
                  .onItem()
                  .transform(
                      reservation ->
                          CreateReservationResponse.newBuilder()
                              .setInfo(reservationInfoTransformFunction().apply(reservation))
                              .build());
            });
  }

  @Override
  public Uni<DeleteReservationResponse> deleteReservation(DeleteReservationRequest request) {
    return reservationRepository
        .getById(UUID.fromString(request.getId()))
        .flatMap(
            reservation ->
                reservationRepository
                    .deleteById(reservation.id())
                    .onItem()
                    .transform(unused -> reservation))
        .onItem()
        .transform(
            reservation ->
                DeleteReservationResponse.newBuilder()
                    .setInfo(reservationInfoTransformFunction().apply(reservation))
                    .build());
  }

  public static Function<ReservationEntity, ReservationInfo> reservationInfoTransformFunction() {
    return reservationEntity -> {
      List<Endorsement> endorsements =
          reservationEntity.restrictions().stream()
              .map(e -> Endorsement.valueOf(e.toString()))
              .toList();
      List<TableInfo> tables =
          reservationEntity.tables().stream()
              .map(t -> TableInfo.newBuilder().setSize(t.size()).setQuantity(t.quantity()).build())
              .toList();
      return ReservationInfo.newBuilder()
          .setId(reservationEntity.id().toString())
          .setRestaurant(restaurantInfoTransformFunction().apply(reservationEntity.restaurant()))
          .setName(reservationEntity.name())
          .setSize(reservationEntity.size())
          .setIsActive(reservationEntity.isActive())
          .addAllRestrictions(endorsements)
          .addAllTables(tables)
          .build();
    };
  }
}
