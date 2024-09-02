package com.bkuberek.bookings.db.repositories;

import com.bkuberek.Constants;
import com.bkuberek.bookings.db.Endorsement;
import com.bkuberek.bookings.db.dao.RestaurantDao;
import com.bkuberek.bookings.db.entities.RestaurantEntity;
import com.bkuberek.bookings.db.entities.RestaurantTableAvailability;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;
import org.jdbi.v3.core.async.JdbiExecutor;

@ApplicationScoped
public class RestaurantRepository {

  private final JdbiExecutor jdbi;

  @Inject
  public RestaurantRepository(JdbiExecutor jdbiExecutor) {
    this.jdbi = jdbiExecutor;
  }

  Uni<List<RestaurantEntity>> listAll() {
    return Uni.createFrom()
        .completionStage(() -> jdbi.withExtension(RestaurantDao.class, RestaurantDao::listAll));
  }

  Uni<RestaurantEntity> getById(UUID id) {
    return Uni.createFrom()
        .completionStage(() -> jdbi.withExtension(RestaurantDao.class, dao -> dao.getById(id)));
  }

  Uni<List<RestaurantEntity>> getByIds(List<UUID> ids) {
    return Uni.createFrom()
        .completionStage(() -> jdbi.withExtension(RestaurantDao.class, dao -> dao.getByIds(ids)));
  }

  Uni<List<RestaurantEntity>> findByName(List<String> names) {
    return Uni.createFrom()
        .completionStage(
            () -> jdbi.withExtension(RestaurantDao.class, dao -> dao.findByName(names)));
  }

  Uni<List<RestaurantEntity>> findRestaurantsByEndorsement(List<Endorsement> endorsements) {
    return Uni.createFrom()
        .completionStage(
            () ->
                jdbi.withExtension(
                    RestaurantDao.class, dao -> dao.findRestaurantsByEndorsement(endorsements)));
  }

  Uni<RestaurantTableAvailability> getAvailableTables(
      UUID restaurantId, Integer size, ZonedDateTime time) {
    return Uni.createFrom()
        .completionStage(
            () ->
                jdbi.withExtension(
                    RestaurantDao.class,
                    dao ->
                        dao
                            .getAvailableTables(
                                restaurantId,
                                size,
                                time,
                                time.plusMinutes(Constants.RESERVATION_DURATION_MINUTES),
                                Constants.RESERVATION_DURATION_SQL_INTERVAL)
                            .stream()
                            .findFirst()
                            .orElse(null)));
  }

  Uni<List<RestaurantTableAvailability>> findRestaurantsWithAvailableTable(
      Integer size, ZonedDateTime time) {
    return Uni.createFrom()
        .completionStage(
            () ->
                jdbi.withExtension(
                    RestaurantDao.class,
                    dao ->
                        dao.findRestaurantsWithAvailableTable(
                            size,
                            time,
                            time.plusMinutes(Constants.RESERVATION_DURATION_MINUTES),
                            Constants.RESERVATION_DURATION_SQL_INTERVAL)));
  }

  Uni<List<RestaurantTableAvailability>> findRestaurantsWithAvailableTableAndRestrictions(
      Integer size, List<Endorsement> restrictions, ZonedDateTime time) {
    return Uni.createFrom()
        .completionStage(
            () ->
                jdbi.withExtension(
                    RestaurantDao.class,
                    dao ->
                        dao.findRestaurantsWithAvailableTableAndRestrictions(
                            size,
                            restrictions,
                            time,
                            time.plusMinutes(Constants.RESERVATION_DURATION_MINUTES),
                            Constants.RESERVATION_DURATION_SQL_INTERVAL)));
  }
}
