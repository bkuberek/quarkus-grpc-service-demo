package com.bkuberek.bookings.db.mapper;

import com.bkuberek.bookings.db.entities.RestaurantTableEntity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

public class RestaurantTableRowMapper implements RowMapper<RestaurantTableEntity> {

  @Override
  public RestaurantTableEntity map(ResultSet rs, StatementContext ctx) throws SQLException {
    return new RestaurantTableEntity(
        UUID.fromString(rs.getString("t_restaurant_id")),
        rs.getInt("t_size"),
        rs.getInt("t_quantity"));
  }
}
