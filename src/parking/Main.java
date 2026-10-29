package parking;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/** Console menu for the parking system. Run this class to use the app. */
public class Main {
    private static final String DATA_FILE = "data/parking.csv";
    private static final int CAR_SLOTS = 5;
    private static final int BIKE_SLOTS = 5;

    public static void main(String[] args) {
        ParkingLot lot = new ParkingLot(CAR_SLOTS, BIKE_SLOTS);

        // Load previously saved data (if any)
        try {
            int loaded = CsvStorage.load(lot, DATA_FILE);
            if (loaded > 0) {
                System.out.println("Loaded " + loaded + " ticket(s) from " + DATA_FILE);
            }
        } catch (IOException | RuntimeException e) {
            System.out.println("Could not load data: " + e.getMessage());
        }

        Scanner sc = new Scanner(System.in);
        boolean running = true;
        while (running) {
            printMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1": parkVehicle(lot, sc); break;
                case "2": exitVehicle(lot, sc); break;
                case "3": showAvailability(lot); break;
                case "4": searchVehicle(lot, sc); break;
                case "5": showParked(lot); break;
                case "6": save(lot); break;
                case "0":
                    save(lot); // auto-save on exit
                    running = false;
                    break;
                default: System.out.println("Invalid choice, try again.");
            }
        }
        System.out.println("Goodbye!");
    }

    private static void printMenu() {
        System.out.println("\n===== CAR PARKING SYSTEM =====");
        System.out.println("1. Park a vehicle");
        System.out.println("2. Exit a vehicle (pay fee)");
        System.out.println("3. Show available slots");
        System.out.println("4. Search by vehicle number");
        System.out.println("5. Show currently parked vehicles");
        System.out.println("6. Save data");
        System.out.println("0. Save & quit");
        System.out.print("Choose: ");
    }

    private static void parkVehicle(ParkingLot lot, Scanner sc) {
        System.out.print("Vehicle type (car/bike): ");
        VehicleType type = VehicleType.fromString(sc.nextLine());
        if (type == null) {
            System.out.println("Unknown vehicle type.");
            return;
        }
        System.out.print("Vehicle number: ");
        String number = sc.nextLine().trim();
        if (number.isEmpty()) {
            System.out.println("Vehicle number cannot be empty.");
            return;
        }
        Ticket ticket = lot.park(new Vehicle(number, type));
        if (ticket == null) {
            System.out.println("Cannot park: no free " + type + " slot, or vehicle is already parked.");
        } else {
            System.out.println("Parked! " + ticket);
        }
    }

    private static void exitVehicle(ParkingLot lot, Scanner sc) {
        System.out.print("Ticket id: ");
        try {
            int id = Integer.parseInt(sc.nextLine().trim());
            Ticket ticket = lot.exit(id);
            if (ticket == null) {
                System.out.println("Invalid ticket, or vehicle already left.");
            } else {
                System.out.println("Vehicle exited. " + ticket);
                System.out.println("Please pay Rs." + ticket.getFee());
            }
        } catch (NumberFormatException e) {
            System.out.println("Ticket id must be a number.");
        }
    }

    private static void showAvailability(ParkingLot lot) {
        for (VehicleType type : VehicleType.values()) {
            System.out.printf("%-5s : %d free of %d (Rs.%d/hour)%n", type,
                    lot.availableSlots(type), lot.totalSlots(type), type.getRatePerHour());
        }
    }

    private static void searchVehicle(ParkingLot lot, Scanner sc) {
        System.out.print("Enter vehicle number (full or partial): ");
        List<Ticket> results = lot.searchByVehicle(sc.nextLine());
        if (results.isEmpty()) {
            System.out.println("No records found.");
        }
        for (Ticket t : results) {
            System.out.println(t);
        }
    }

    private static void showParked(ParkingLot lot) {
        List<Ticket> active = lot.getActiveTickets();
        if (active.isEmpty()) {
            System.out.println("Parking lot is empty.");
        }
        for (Ticket t : active) {
            System.out.println(t);
        }
    }

    private static void save(ParkingLot lot) {
        try {
            CsvStorage.save(lot, DATA_FILE);
            System.out.println("Data saved to " + DATA_FILE);
        } catch (IOException e) {
            System.out.println("Could not save data: " + e.getMessage());
        }
    }
}
