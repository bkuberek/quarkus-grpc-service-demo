package com.bkuberek.bookings;

import java.time.LocalTime;
import org.apache.commons.lang3.tuple.Pair;

public class Constants {
  private Constants() {}

  public static final Long RESERVATION_DURATION_MINUTES = 120L;
  public static final String RESERVATION_DURATION_SQL_INTERVAL = "120 minutes";
  public static final Pair<LocalTime, LocalTime> RESTAURANT_HOURS_OF_OPERATION =
      Pair.of(LocalTime.of(6, 0, 0), LocalTime.MIDNIGHT);
}
