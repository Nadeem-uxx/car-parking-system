package parking;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The main class that holds all slots and tickets and implements the
 * parking operations: park, exit, availability and search.
 */
public class ParkingLot {
    private final List<ParkingSlot> slots = new ArrayList<>();
    // All tickets ever issued (active + completed), keyed by ticket id
    private final Map<Integer, Ticket> tickets = new LinkedHashMap<>();
    private int nextTicketId = 1;

    /** Slots 1..carSlots are for cars, the rest are for bikes. */
    public ParkingLot(int carSlots, int bikeSlots) {
        int number = 1;
        for (int i = 0; i < carSlots; i++) {
            slots.add(new ParkingSlot(number++, VehicleType.CAR));
        }
        for (int i = 0; i < bikeSlots; i++) {
            slots.add(new ParkingSlot(number++, VehicleType.BIKE));
        }
    }

    /** Parks a vehicle using the current time. Returns the ticket, or null if not possible. */
    public Ticket park(Vehicle vehicle) {
        return park(vehicle, LocalDateTime.now().withNano(0));
    }

    /** Same as above but with an explicit entry time (useful for tests/demo). */
    public Ticket park(Vehicle vehicle, LocalDateTime entryTime) {
        // Don't allow the same vehicle to be parked twice
        if (findActiveTicket(vehicle.getNumber()) != null) {
            return null;
        }
        ParkingSlot slot = findFreeSlot(vehicle.getType());
        if (slot == null) {
            return null; // lot is full for this vehicle type
        }
        Ticket ticket = new Ticket(nextTicketId++, vehicle, slot.getNumber(), entryTime);
        slot.occupy(ticket);
        tickets.put(ticket.getId(), ticket);
        return ticket;
    }

    /** Vehicle leaves now. Returns the closed ticket (with fee) or null if ticket is invalid. */
    public Ticket exit(int ticketId) {
        return exit(ticketId, LocalDateTime.now().withNano(0));
    }

    public Ticket exit(int ticketId, LocalDateTime exitTime) {
        Ticket ticket = tickets.get(ticketId);
        if (ticket == null || !ticket.isActive()) {
            return null;
        }
        int fee = FeeCalculator.calculateFee(ticket.getVehicle().getType(),
                ticket.getEntryTime(), exitTime);
        ticket.close(exitTime, fee);
        slots.get(ticket.getSlotNumber() - 1).free();
        return ticket;
    }

    /** Number of free slots for the given vehicle type. */
    public int availableSlots(VehicleType type) {
        int count = 0;
        for (ParkingSlot slot : slots) {
            if (slot.getType() == type && slot.isFree()) {
                count++;
            }
        }
        return count;
    }

    public int totalSlots(VehicleType type) {
        int count = 0;
        for (ParkingSlot slot : slots) {
            if (slot.getType() == type) {
                count++;
            }
        }
        return count;
    }

    /** Finds the ticket of a vehicle that is currently parked (or null). */
    public Ticket findActiveTicket(String vehicleNumber) {
        String wanted = vehicleNumber.replace(" ", "").toUpperCase();
        for (Ticket t : tickets.values()) {
            if (t.isActive() && t.getVehicle().getNumber().equals(wanted)) {
                return t;
            }
        }
        return null;
    }

    /** All tickets (active and past) that belong to a vehicle number. */
    public List<Ticket> searchByVehicle(String vehicleNumber) {
        String wanted = vehicleNumber.replace(" ", "").toUpperCase();
        List<Ticket> result = new ArrayList<>();
        for (Ticket t : tickets.values()) {
            if (t.getVehicle().getNumber().contains(wanted)) { // partial match allowed
                result.add(t);
            }
        }
        return result;
    }

    public List<Ticket> getActiveTickets() {
        List<Ticket> result = new ArrayList<>();
        for (Ticket t : tickets.values()) {
            if (t.isActive()) {
                result.add(t);
            }
        }
        return result;
    }

    public List<Ticket> getAllTickets() {
        return new ArrayList<>(tickets.values());
    }

    public List<ParkingSlot> getSlots() {
        return slots;
    }

    public int getNextTicketId() {
        return nextTicketId;
    }

    /**
     * Used when loading from file: puts an already-created ticket back into the lot.
     * Active tickets re-occupy their slot.
     */
    void restoreTicket(Ticket ticket) {
        tickets.put(ticket.getId(), ticket);
        if (ticket.isActive()) {
            slots.get(ticket.getSlotNumber() - 1).occupy(ticket);
        }
        if (ticket.getId() >= nextTicketId) {
            nextTicketId = ticket.getId() + 1;
        }
    }

    private ParkingSlot findFreeSlot(VehicleType type) {
        for (ParkingSlot slot : slots) {
            if (slot.getType() == type && slot.isFree()) {
                return slot; // first free slot = nearest to entrance
            }
        }
        return null;
    }
}
