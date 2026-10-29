package parking;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * A non-interactive demo: parks some vehicles with fixed times,
 * exits them, saves to CSV, then loads the CSV into a new lot.
 * Run with: java -cp out parking.Demo
 */
public class Demo {
    public static void main(String[] args) throws IOException {
        ParkingLot lot = new ParkingLot(2, 2);
        LocalDateTime nine = LocalDateTime.of(2026, 1, 15, 9, 0);

        Ticket t1 = lot.park(new Vehicle("UP32 AB 1234", VehicleType.CAR), nine);
        Ticket t2 = lot.park(new Vehicle("UP32 CD 5678", VehicleType.BIKE), nine.plusMinutes(15));
        Ticket t3 = lot.park(new Vehicle("DL01 EF 9999", VehicleType.CAR), nine.plusMinutes(30));
        System.out.println("Parked:\n  " + t1 + "\n  " + t2 + "\n  " + t3);

        System.out.println("\nThird car tries to park in a full car area -> "
                + lot.park(new Vehicle("MH12 ZZ 0001", VehicleType.CAR), nine.plusHours(1)));

        System.out.println("\nFree car slots: " + lot.availableSlots(VehicleType.CAR)
                + ", free bike slots: " + lot.availableSlots(VehicleType.BIKE));

        // Car parked 2h30m -> billed 3 hours -> 3 x 20 = Rs.60
        System.out.println("\nExit: " + lot.exit(t1.getId(), nine.plusHours(2).plusMinutes(30)));
        // Bike parked 20 minutes -> billed 1 hour -> Rs.10
        System.out.println("Exit: " + lot.exit(t2.getId(), nine.plusMinutes(35)));

        System.out.println("\nSearch 'EF99':");
        lot.searchByVehicle("EF99").forEach(t -> System.out.println("  " + t));

        String file = "data/demo.csv";
        CsvStorage.save(lot, file);
        ParkingLot reloaded = new ParkingLot(2, 2);
        int n = CsvStorage.load(reloaded, file);
        System.out.println("\nSaved and reloaded " + n + " tickets from " + file
                + "; currently parked: " + reloaded.getActiveTickets().size());
    }
}
