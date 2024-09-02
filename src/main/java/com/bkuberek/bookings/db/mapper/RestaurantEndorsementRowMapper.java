package com.bkuberek.bookings.db.mapper;

import com.bkuberek.bookings.db.Endorsement;
import com.bkuberek.bookings.db.entities.RestaurantEndorsementEntity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

public class RestaurantEndorsementRowMapper implements RowMapper<RestaurantEndorsementEntity> {

  @Override
  public RestaurantEndorsementEntity map(ResultSet rs, StatementContext ctx) throws SQLException {
    return new RestaurantEndorsementEntity(
        UUID.fromString(rs.getString("e_restaurant_id")),
        Endorsement.valueOf(rs.getString("e_endorsement")));
  }
}
