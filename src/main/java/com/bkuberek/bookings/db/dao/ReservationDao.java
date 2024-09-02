package com.bkuberek.bookings.db.dao;

import com.bkuberek.bookings.db.entities.*;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.jdbi.v3.core.result.LinkedHashMapRowReducer;
import org.jdbi.v3.core.result.RowView;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.customizer.BindList;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.statement.UseRowReducer;
import org.jdbi.v3.sqlobject.transaction.Transaction;

public interface ReservationDao {
  @SqlQuery(
      """
              SELECT bb.id               as b_id,
                     bb.restaurant_id    as b_restaurant_id,
                     bb.name             as b_name,
                     bb.size             as b_size,
                     bb.is_active        as b_is_active,
                     bb.reservation_time as b_reservation_time,
                     bb.created_time     as b_created_time,
                     bb.updated_time     as b_updated_time,
                     bb.restrictions     as b_restrictions,
                     rr.id               as r_id,
                     rr.name             as r_name,
                     rr.created_time     as r_created_time,
                     rr.updated_time     as r_updated_time,
                     bt.reservation_id   as bt_reservation_id,
                     bt.size             as bt_size,
                     bt.quantity         as bt_quantity
              FROM reservation AS bb
                       INNER JOIN restaurant AS rr ON rr.id = bb.restaurant_id
                       INNER JOIN reservation_table AS bt ON bt.reservation_id = bb.id
              WHERE bb.name = :name
              ORDER BY 3, 4;
          """)
  @UseRowReducer(ReservationRowReducer.class)
  Set<ReservationEntity> listAllForUser(@Bind("name") String name);

  @SqlQuery(
      """
              SELECT bb.id               as b_id,
                     bb.restaurant_id    as b_restaurant_id,
                     bb.name             as b_name,
                     bb.size             as b_size,
                     bb.is_active        as b_is_active,
                     bb.reservation_time as b_reservation_time,
                     bb.created_time     as b_created_time,
                     bb.updated_time     as b_updated_time,
                     bb.restrictions     as b_restrictions,
                     rr.id               as r_id,
                     rr.name             as r_name,
                     rr.created_time     as r_created_time,
                     rr.updated_time     as r_updated_time,
                     bt.reservation_id   as bt_reservation_id,
                     bt.size             as bt_size,
                     bt.quantity         as bt_quantity
              FROM reservation AS bb
                       INNER JOIN restaurant AS rr ON rr.id = bb.restaurant_id
                       INNER JOIN reservation_table AS bt ON bt.reservation_id = bb.id
              WHERE bb.name = :name
              ORDER BY 3, 4;
          """)
  @UseRowReducer(ReservationRowReducer.class)
  Set<ReservationEntity> listAllForUserAndRestaurant(
      @Bind("name") String name, @Bind("restaurant_id") UUID restaurantId);

  default ReservationEntity getById(UUID id) {
    return getByIds(Set.of(id)).stream().findFirst().orElse(null);
  }

  @SqlQuery(
      """
          SELECT bb.id               as b_id,
                 bb.restaurant_id    as b_restaurant_id,
                 bb.name             as b_name,
                 bb.size             as b_size,
                 bb.is_active        as b_is_active,
                 bb.reservation_time as b_reservation_time,
                 bb.created_time     as b_created_time,
                 bb.updated_time     as b_updated_time,
                 bb.restrictions     as b_restrictions,
                 rr.id               as r_id,
                 rr.name             as r_name,
                 rr.created_time     as r_created_time,
                 rr.updated_time     as r_updated_time,
                 bt.reservation_id   as bt_reservation_id,
                 bt.size             as bt_size,
                 bt.quantity         as bt_quantity
          FROM reservation AS bb
                   INNER JOIN restaurant AS rr ON rr.id = bb.restaurant_id
                   INNER JOIN reservation_table AS bt ON bt.reservation_id = bb.id
          WHERE bb.id IN (<ids>)
          ORDER BY 3, 4;
      """)
  @UseRowReducer(ReservationRowReducer.class)
  Set<ReservationEntity> getByIds(@BindList("ids") Set<UUID> ids);

  @SqlUpdate(
      """
          INSERT INTO reservation (id, restaurant_id, name, size, restrictions, reservation_time)
          VALUES (:b.id, :b.restaurant.id, :b.name, :b.size, array_to_string(:b.restrictions, ','), :b.reservationTime)
      """)
  void saveReservation(@BindBean("b") ReservationEntity entity);

  @SqlUpdate(
      """
          INSERT INTO reservation_table (reservation_id, size, quantity)
          VALUES (:t.reservationId, :t.size, :t.quantity)
      """)
  void saveReservationTable(@BindBean("t") ReservationTableEntity entity);

  @Transaction
  default void createReservation(ReservationEntity reservationEntity) {
    saveReservation(reservationEntity);
    reservationEntity.tables().forEach(this::saveReservationTable);
  }

  default void deleteById(UUID id) {
    deleteByIds(Set.of(id));
  }

  @Transaction
  @SqlUpdate("DELETE FROM reservation WHERE id IN (<ids>)")
  void deleteByIds(@BindList("ids") Set<UUID> ids);

  class ReservationRowReducer implements LinkedHashMapRowReducer<UUID, ReservationEntity> {
    @Override
    public void accumulate(Map<UUID, ReservationEntity> container, RowView rowView) {
      if (rowView != null && container != null) {
        ReservationEntity reservation =
            container.computeIfAbsent(
                rowView.getColumn("b_id", UUID.class),
                uuid -> rowView.getRow(ReservationEntity.class));

        if (rowView.getColumn("bt_reservation_id", UUID.class) != null) {
          reservation.tables().add(rowView.getRow(ReservationTableEntity.class));
        }

        if (rowView.getColumn("r_id", UUID.class) != null) {
          RestaurantEntity restaurant = rowView.getRow(RestaurantEntity.class);
          container.put(reservation.id(), reservation.withRestaurant(restaurant));
        }
      }
    }
  }
}
