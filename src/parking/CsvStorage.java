package parking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

/**
 * Saves and loads the parking lot's tickets to a simple CSV file.
 *
 * File format (first line is a header):
 * ticketId,vehicleNumber,type,slotNumber,entryTime,exitTime,fee
 * exitTime is empty and fee is 0 for vehicles that are still parked.
 */
public class CsvStorage {
    private static final String HEADER = "ticketId,vehicleNumber,type,slotNumber,entryTime,exitTime,fee";

    public static void save(ParkingLot lot, String fileName) throws IOException {
        Path path = Paths.get(fileName);
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent()); // make sure the folder exists
        }
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(HEADER);
            writer.newLine();
            for (Ticket t : lot.getAllTickets()) {
                String exit = t.getExitTime() == null ? "" : t.getExitTime().toString();
                writer.write(String.join(",",
                        String.valueOf(t.getId()),
                        t.getVehicle().getNumber(),
                        t.getVehicle().getType().name(),
                        String.valueOf(t.getSlotNumber()),
                        t.getEntryTime().toString(),
                        exit,
                        String.valueOf(t.getFee())));
                writer.newLine();
            }
        }
    }

    /**
     * Loads tickets from the CSV into the given (empty) lot.
     * Returns how many tickets were loaded. If the file does not exist, returns 0.
     */
    public static int load(ParkingLot lot, String fileName) throws IOException {
        Path path = Paths.get(fileName);
        if (!Files.exists(path)) {
            return 0;
        }
        int count = 0;
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line = reader.readLine(); // skip header
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                String[] p = line.split(",", -1); // -1 keeps empty trailing fields
                if (p.length < 7) {
                    System.out.println("Skipping bad line: " + line);
                    continue;
                }
                Vehicle vehicle = new Vehicle(p[1], VehicleType.valueOf(p[2]));
                Ticket ticket = new Ticket(Integer.parseInt(p[0]), vehicle,
                        Integer.parseInt(p[3]), LocalDateTime.parse(p[4]));
                if (!p[5].isEmpty()) {
                    ticket.close(LocalDateTime.parse(p[5]), Integer.parseInt(p[6]));
                }
                lot.restoreTicket(ticket);
                count++;
            }
        }
        return count;
    }
}
