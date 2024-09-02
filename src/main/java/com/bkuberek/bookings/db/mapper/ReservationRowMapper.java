package com.bkuberek.bookings.db.mapper;

import com.bkuberek.bookings.db.Endorsement;
import com.bkuberek.bookings.db.entities.ReservationEntity;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

public class ReservationRowMapper implements RowMapper<ReservationEntity> {

  @Override
  public ReservationEntity map(ResultSet rs, StatementContext ctx) throws SQLException {
    return new ReservationEntity(
        UUID.fromString(rs.getString("b_id")),
        rs.getString("b_name"),
        rs.getInt("b_size"),
        rs.getBoolean("b_is_active"),
        null,
        Optional.ofNullable(rs.getTimestamp("b_reservation_time"))
            .map(timestamp -> timestamp.toInstant().atZone(ZoneId.systemDefault()))
            .orElse(null),
        Optional.ofNullable(rs.getTimestamp("b_created_time"))
            .map(timestamp -> timestamp.toInstant().atZone(ZoneId.systemDefault()))
            .orElse(null),
        Optional.ofNullable(rs.getTimestamp("b_updated_time"))
            .map(timestamp -> timestamp.toInstant().atZone(ZoneId.systemDefault()))
            .orElse(null),
        Arrays.stream(rs.getString("b_restrictions").split(","))
            .map(Endorsement::valueOf)
            .collect(Collectors.toSet()));
  }
}
