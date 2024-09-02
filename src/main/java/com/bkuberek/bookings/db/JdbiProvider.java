package com.bkuberek.bookings.db;

import com.bkuberek.bookings.db.entities.*;
import com.bkuberek.bookings.db.mapper.*;
import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.async.JdbiExecutor;
import org.jdbi.v3.core.statement.Slf4JSqlLogger;
import org.jdbi.v3.postgres.PostgresPlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

@ApplicationScoped
public class JdbiProvider {

  private final AgroalDataSource ds;

  @Inject
  public JdbiProvider(AgroalDataSource ds) {
    this.ds = ds;
  }

  @Singleton
  @Produces
  public Jdbi jdbi() {
    Jdbi jdbi = Jdbi.create(ds);
    jdbi.setSqlLogger(new Slf4JSqlLogger());
    jdbi.installPlugin(new SqlObjectPlugin());
    jdbi.installPlugin(new PostgresPlugin());
    jdbi.registerRowMapper(RestaurantEntity.class, new RestaurantRowMapper());
    jdbi.registerRowMapper(RestaurantTableEntity.class, new RestaurantTableRowMapper());
    jdbi.registerRowMapper(RestaurantEndorsementEntity.class, new RestaurantEndorsementRowMapper());
    jdbi.registerRowMapper(ReservationEntity.class, new ReservationRowMapper());
    jdbi.registerRowMapper(ReservationTableEntity.class, new ReservationTableRowMapper());
    return jdbi;
  }

  @Singleton
  @Produces
  public JdbiExecutor jdbiExecutor() {
    Executor executor = Executors.newFixedThreadPool(8);
    return JdbiExecutor.create(jdbi(), executor);
  }
}
