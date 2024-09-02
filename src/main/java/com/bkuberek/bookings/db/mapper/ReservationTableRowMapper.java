package com.bkuberek.bookings.db.mapper;

import com.bkuberek.bookings.db.entities.ReservationTableEntity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

public class ReservationTableRowMapper implements RowMapper<ReservationTableEntity> {

  @Override
  public ReservationTableEntity map(ResultSet rs, StatementContext ctx) throws SQLException {
    return new ReservationTableEntity(
        UUID.fromString(rs.getString("bt_reservation_id")),
        rs.getInt("bt_size"),
        rs.getInt("bt_quantity"));
  }
}
