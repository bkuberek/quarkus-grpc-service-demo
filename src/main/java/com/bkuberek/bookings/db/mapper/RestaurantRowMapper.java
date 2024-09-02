package com.bkuberek.bookings.db.mapper;

import com.bkuberek.bookings.db.entities.RestaurantEntity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

public class RestaurantRowMapper implements RowMapper<RestaurantEntity> {

  @Override
  public RestaurantEntity map(ResultSet rs, StatementContext ctx) throws SQLException {
    return new RestaurantEntity(
        UUID.fromString(rs.getString("r_id")),
        rs.getString("r_name"),
        Optional.ofNullable(rs.getTimestamp("r_created_time"))
            .map(timestamp -> timestamp.toInstant().atZone(ZoneId.systemDefault()))
            .orElse(null),
        Optional.ofNullable(rs.getTimestamp("r_updated_time"))
            .map(timestamp -> timestamp.toInstant().atZone(ZoneId.systemDefault()))
            .orElse(null));
  }
}
