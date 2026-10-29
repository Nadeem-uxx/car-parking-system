package parking;

/** A vehicle is identified by its registration number and its type. */
public class Vehicle {
    private final String number;
    private final VehicleType type;

    public Vehicle(String number, VehicleType type) {
        // Store numbers in upper case without spaces so "mh12 ab 1234" == "MH12AB1234"
        this.number = number.replace(" ", "").toUpperCase();
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public VehicleType getType() {
        return type;
    }

    @Override
    public String toString() {
        return type + " " + number;
    }
}
