package parking;

/** One physical parking space. It only accepts one vehicle type. */
public class ParkingSlot {
    private final int number;
    private final VehicleType type;
    private Ticket currentTicket; // null means the slot is free

    public ParkingSlot(int number, VehicleType type) {
        this.number = number;
        this.type = type;
    }

    public boolean isFree() {
        return currentTicket == null;
    }

    public void occupy(Ticket ticket) {
        this.currentTicket = ticket;
    }

    public void free() {
        this.currentTicket = null;
    }

    public int getNumber() { return number; }
    public VehicleType getType() { return type; }
    public Ticket getCurrentTicket() { return currentTicket; }
}
