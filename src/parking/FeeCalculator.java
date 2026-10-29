package parking;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Fee rule: charge per hour, and any started hour counts as a full hour.
 * Minimum charge is 1 hour.
 *   e.g. 10 min -> 1 hour, 1h 01m -> 2 hours, 3h 00m -> 3 hours
 */
public class FeeCalculator {

    public static long billableHours(LocalDateTime entry, LocalDateTime exit) {
        long minutes = Duration.between(entry, exit).toMinutes();
        if (minutes <= 0) {
            return 1; // minimum one hour
        }
        return (minutes + 59) / 60; // round up to the next whole hour
    }

    public static int calculateFee(VehicleType type, LocalDateTime entry, LocalDateTime exit) {
        return (int) billableHours(entry, exit) * type.getRatePerHour();
    }
}
