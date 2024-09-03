package com.bkuberek.bookings.db.dao;

import com.bkuberek.bookings.db.Endorsement;
import com.bkuberek.bookings.db.entities.RestaurantEndorsementEntity;
import com.bkuberek.bookings.db.entities.RestaurantEntity;
import com.bkuberek.bookings.db.entities.RestaurantTableAvailability;
import com.bkuberek.bookings.db.entities.RestaurantTableEntity;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.jdbi.v3.core.result.LinkedHashMapRowReducer;
import org.jdbi.v3.core.result.RowView;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.customizer.Define;
import org.jdbi.v3.sqlobject.locator.UseClasspathSqlLocator;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.UseRowReducer;
import org.jdbi.v3.stringtemplate4.UseStringTemplateEngine;

public interface RestaurantDao {
  @SqlQuery(
      """
                SELECT r.id             as r_id,
                       r.name           as r_name,
                       r.created_time   as r_created_time,
                       r.updated_time   as r_updated_time,
                       re.restaurant_id as e_restaurant_id,
                       re.endorsement   as e_endorsement,
                       rt.restaurant_id as t_restaurant_id,
                       rt.size          as t_size,
                       rt.quantity      as t_quantity
                FROM restaurant AS r
                         LEFT JOIN restaurant_endorsement AS re ON re.restaurant_id = r.id
                         LEFT JOIN restaurant_table AS rt ON rt.restaurant_id = r.id
                ORDER BY r_name, t_size;
            """)
  @UseRowReducer(RestaurantRowReducer.class)
  public List<RestaurantEntity> listAll();

  default RestaurantEntity getById(UUID id) {
    return getByIds(List.of(id)).stream().findFirst().orElse(null);
  }

  @SqlQuery(
      """
                SELECT r.id             as r_id,
                       r.name           as r_name,
                       r.created_time   as r_created_time,
                       r.updated_time   as r_updated_time,
                       re.restaurant_id as e_restaurant_id,
                       re.endorsement   as e_endorsement,
                       rt.restaurant_id as t_restaurant_id,
                       rt.size          as t_size,
                       rt.quantity      as t_quantity
                FROM restaurant AS r
                         LEFT JOIN restaurant_endorsement AS re ON re.restaurant_id = r.id
                         LEFT JOIN restaurant_table AS rt ON rt.restaurant_id = r.id
                WHERE r.id in (<ids>)
                ORDER BY r_name, t_size;
            """)
  @UseRowReducer(RestaurantRowReducer.class)
  List<RestaurantEntity> getByIds(@BindList("ids") List<UUID> ids);

  @SqlQuery
  @UseClasspathSqlLocator
  @UseStringTemplateEngine
  @UseRowReducer(RestaurantAvailabilityRowReducer.class)
  @RegisterConstructorMapper(RestaurantTableAvailability.class)
  List<RestaurantTableAvailability> getAvailableTables(
      @Bind("restaurant_id") UUID restaurantId,
      @Bind("size") Integer size,
      @Bind("time_start") ZonedDateTime timeStart,
      @Bind("time_stop") ZonedDateTime timeStop,
      @Define("time_interval") String timeInterval);

  @SqlQuery(
      """
                SELECT r.id             as r_id,
                       r.name           as r_name,
                       r.created_time   as r_created_time,
                       r.updated_time   as r_updated_time,
                       re.restaurant_id as e_restaurant_id,
                       re.endorsement   as e_endorsement,
                       rt.restaurant_id as t_restaurant_id,
                       rt.size          as t_size,
                       rt.quantity      as t_quantity
                FROM restaurant AS r
                         LEFT JOIN restaurant_endorsement AS re ON re.restaurant_id = r.id
                         LEFT JOIN restaurant_table AS rt ON rt.restaurant_id = r.id
                WHERE r.name in (<names>)
                ORDER BY r_name, t_size;
            """)
  @UseRowReducer(RestaurantRowReducer.class)
  List<RestaurantEntity> findByName(@BindList("names") List<String> names);

  @SqlQuery(
      """
                SELECT r.id
                FROM restaurant r
                INNER JOIN restaurant_endorsement e ON e.restaurant_id = r.id
                WHERE e.endorsement in (<endorsements>)
            """)
  List<UUID> findRestaurantIdsByEndorsement(
      @BindList("endorsements") List<Endorsement> endorsements);

  default List<RestaurantEntity> findRestaurantsByEndorsement(List<Endorsement> endorsements) {
    return getByIds(findRestaurantIdsByEndorsement(endorsements));
  }

  @SqlQuery
  @UseClasspathSqlLocator
  @UseStringTemplateEngine
  @UseRowReducer(RestaurantAvailabilityRowReducer.class)
  @RegisterConstructorMapper(RestaurantTableAvailability.class)
  List<RestaurantTableAvailability> findRestaurantsWithAvailableTable(
      @Bind("size") Integer size,
      @Bind("time_start") ZonedDateTime timeStart,
      @Bind("time_stop") ZonedDateTime timeStop,
      @Define("time_interval") String timeInterval);

  @SqlQuery
  @UseClasspathSqlLocator
  @UseStringTemplateEngine
  @UseRowReducer(RestaurantAvailabilityRowReducer.class)
  @RegisterBeanMapper(RestaurantTableAvailability.class)
  List<RestaurantTableAvailability> findRestaurantsWithAvailableTableAndRestrictions(
      @Bind("size") Integer size,
      @BindList("restrictions") List<Endorsement> restrictions,
      @Bind("time_start") ZonedDateTime timeStart,
      @Bind("time_stop") ZonedDateTime timeStop,
      @Define("time_interval") String timeInterval);

  class RestaurantRowReducer implements LinkedHashMapRowReducer<UUID, RestaurantEntity> {
    @Override
    public void accumulate(Map<UUID, RestaurantEntity> container, RowView rowView) {
      if (rowView != null && container != null) {
        RestaurantEntity restaurant =
            container.computeIfAbsent(
                rowView.getColumn("r_id", UUID.class),
                uuid -> rowView.getRow(RestaurantEntity.class));

        if (rowView.getColumn("t_restaurant_id", UUID.class) != null) {
          restaurant.tables().add(rowView.getRow(RestaurantTableEntity.class));
        }

        if (rowView.getColumn("e_restaurant_id", UUID.class) != null) {
          restaurant.endorsements().add(rowView.getRow(RestaurantEndorsementEntity.class));
        }
      }
    }
  }

  class RestaurantAvailabilityRowReducer
      implements LinkedHashMapRowReducer<UUID, RestaurantTableAvailability> {
    @Override
    public void accumulate(Map<UUID, RestaurantTableAvailability> container, RowView rowView) {
      if (rowView != null && container != null) {
        RestaurantTableAvailability availability =
            container.computeIfAbsent(
                rowView.getColumn("id", UUID.class),
                uuid -> rowView.getRow(RestaurantTableAvailability.class));

        Integer size = rowView.getColumn("size", Integer.class);
        availability
            .tables()
            .add(
                new RestaurantTableEntity(
                    availability.id(), size, rowView.getColumn("total", Integer.class)));
        availability
            .occupiedTables()
            .add(
                new RestaurantTableEntity(
                    availability.id(), size, rowView.getColumn("occupied", Integer.class)));
        availability
            .availableTables()
            .add(
                new RestaurantTableEntity(
                    availability.id(), size, rowView.getColumn("available", Integer.class)));
      }
    }
  }
}
