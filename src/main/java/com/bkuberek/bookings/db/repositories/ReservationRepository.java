package com.bkuberek.bookings.db.repositories;

import com.bkuberek.bookings.db.dao.ReservationDao;
import com.bkuberek.bookings.db.entities.ReservationEntity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Set;
import java.util.UUID;
import org.jdbi.v3.core.async.JdbiExecutor;

@ApplicationScoped
public class ReservationRepository {

  private final JdbiExecutor jdbi;

  @Inject
  public ReservationRepository(JdbiExecutor jdbiExecutor) {
    this.jdbi = jdbiExecutor;
  }

  Uni<Set<ReservationEntity>> listAllForUser(String name) {
    return Uni.createFrom()
        .completionStage(
            () -> jdbi.withExtension(ReservationDao.class, dao -> dao.listAllForUser(name)));
  }

  Uni<Set<ReservationEntity>> listAllForUserAndRestaurant(String name, UUID restaurantId) {
    return Uni.createFrom()
        .completionStage(
            () ->
                jdbi.withExtension(
                    ReservationDao.class,
                    dao -> dao.listAllForUserAndRestaurant(name, restaurantId)));
  }

  Uni<ReservationEntity> getById(UUID id) {
    return Uni.createFrom()
        .completionStage(() -> jdbi.withExtension(ReservationDao.class, dao -> dao.getById(id)));
  }

  Uni<Set<ReservationEntity>> getById(Set<UUID> ids) {
    return Uni.createFrom()
        .completionStage(() -> jdbi.withExtension(ReservationDao.class, dao -> dao.getByIds(ids)));
  }

  Uni<ReservationEntity> createReservation(ReservationEntity reservation) {
    return Uni.createFrom()
        .completionStage(
            () ->
                jdbi.useExtension(ReservationDao.class, dao -> dao.createReservation(reservation))
                    .thenApply(unused -> reservation));
  }

  public Uni<Void> deleteById(UUID id) {
    return Uni.createFrom()
        .completionStage(() -> jdbi.useExtension(ReservationDao.class, dao -> dao.deleteById(id)));
  }

  public Uni<Void> deleteByIds(Set<UUID> ids) {
    return Uni.createFrom()
        .completionStage(
            () -> jdbi.useExtension(ReservationDao.class, dao -> dao.deleteByIds(ids)));
  }
}
