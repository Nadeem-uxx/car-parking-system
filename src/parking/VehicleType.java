package parking;

/**
 * The kinds of vehicles our parking lot supports.
 * Each type has its own hourly rate (in rupees).
 */
public enum VehicleType {
    CAR(20),
    BIKE(10);

    private final int ratePerHour;

    VehicleType(int ratePerHour) {
        this.ratePerHour = ratePerHour;
    }

    public int getRatePerHour() {
        return ratePerHour;
    }

    /** Converts user input like "car" / "Bike" into an enum value (or null if invalid). */
    public static VehicleType fromString(String text) {
        if (text == null) {
            return null;
        }
        try {
            return VehicleType.valueOf(text.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
