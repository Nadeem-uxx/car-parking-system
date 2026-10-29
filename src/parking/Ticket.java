package parking;

import java.time.LocalDateTime;

/** A parking ticket: created when a vehicle enters, completed when it leaves. */
public class Ticket {
    private final int id;
    private final Vehicle vehicle;
    private final int slotNumber;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime; // null while the vehicle is still parked
    private int fee;                // 0 until the vehicle exits

    public Ticket(int id, Vehicle vehicle, int slotNumber, LocalDateTime entryTime) {
        this.id = id;
        this.vehicle = vehicle;
        this.slotNumber = slotNumber;
        this.entryTime = entryTime;
    }

    /** Called when the vehicle leaves. */
    public void close(LocalDateTime exitTime, int fee) {
        this.exitTime = exitTime;
        this.fee = fee;
    }

    public boolean isActive() {
        return exitTime == null;
    }

    public int getId() { return id; }
    public Vehicle getVehicle() { return vehicle; }
    public int getSlotNumber() { return slotNumber; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public LocalDateTime getExitTime() { return exitTime; }
    public int getFee() { return fee; }

    @Override
    public String toString() {
        return String.format("Ticket #%d | %s | Slot %d | In: %s%s",
                id, vehicle, slotNumber, entryTime.toString().replace('T', ' '),
                isActive() ? " | PARKED"
                        : " | Out: " + exitTime.toString().replace('T', ' ') + " | Fee: Rs." + fee);
    }
}
