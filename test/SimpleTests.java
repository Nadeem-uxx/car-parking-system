import parking.*;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Tiny test runner without JUnit. Each check() prints PASS/FAIL,
 * and the program exits with code 1 if anything failed.
 * Run with: ./run.sh test
 */
public class SimpleTests {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) throws IOException {
        testFeeCalculation();
        testParkingAndSlots();
        testExitAndFee();
        testSearch();
        testCsvSaveLoad();

        System.out.println("\n" + passed + " passed, " + failed + " failed");
        if (failed > 0) {
            System.exit(1);
        }
    }

    static void testFeeCalculation() {
        LocalDateTime t = LocalDateTime.of(2026, 1, 1, 10, 0);
        check("10 min car = Rs.20 (min 1 hour)",
                FeeCalculator.calculateFee(VehicleType.CAR, t, t.plusMinutes(10)) == 20);
        check("exactly 3 hours car = Rs.60",
                FeeCalculator.calculateFee(VehicleType.CAR, t, t.plusHours(3)) == 60);
        check("3h 01m car = 4 hours = Rs.80",
                FeeCalculator.calculateFee(VehicleType.CAR, t, t.plusMinutes(181)) == 80);
        check("2h bike = Rs.20",
                FeeCalculator.calculateFee(VehicleType.BIKE, t, t.plusHours(2)) == 20);
    }

    static void testParkingAndSlots() {
        ParkingLot lot = new ParkingLot(1, 1);
        check("car parks successfully", lot.park(new Vehicle("A1", VehicleType.CAR)) != null);
        check("car slots now 0", lot.availableSlots(VehicleType.CAR) == 0);
        check("second car rejected (full)", lot.park(new Vehicle("A2", VehicleType.CAR)) == null);
        check("bike still has a slot", lot.availableSlots(VehicleType.BIKE) == 1);
        check("same vehicle cannot park twice", lot.park(new Vehicle("A1", VehicleType.CAR)) == null);
    }

    static void testExitAndFee() {
        ParkingLot lot = new ParkingLot(1, 0);
        LocalDateTime in = LocalDateTime.of(2026, 1, 1, 8, 0);
        Ticket t = lot.park(new Vehicle("B1", VehicleType.CAR), in);
        Ticket closed = lot.exit(t.getId(), in.plusMinutes(90));
        check("fee for 90 min car = Rs.40", closed != null && closed.getFee() == 40);
        check("slot freed after exit", lot.availableSlots(VehicleType.CAR) == 1);
        check("exiting twice is rejected", lot.exit(t.getId()) == null);
        check("unknown ticket is rejected", lot.exit(999) == null);
    }

    static void testSearch() {
        ParkingLot lot = new ParkingLot(2, 1);
        lot.park(new Vehicle("UP32 AB 1234", VehicleType.CAR));
        lot.park(new Vehicle("DL01 XY 7777", VehicleType.BIKE));
        check("search is case/space-insensitive", lot.searchByVehicle("up32ab1234").size() == 1);
        check("partial search works", lot.searchByVehicle("7777").size() == 1);
        check("no match returns empty", lot.searchByVehicle("ZZZ").isEmpty());
    }

    static void testCsvSaveLoad() throws IOException {
        ParkingLot lot = new ParkingLot(2, 2);
        LocalDateTime in = LocalDateTime.of(2026, 1, 1, 8, 0);
        Ticket a = lot.park(new Vehicle("C1", VehicleType.CAR), in);
        lot.park(new Vehicle("C2", VehicleType.BIKE), in);
        lot.exit(a.getId(), in.plusHours(1));

        String file = "data/test-tmp.csv";
        CsvStorage.save(lot, file);
        ParkingLot loaded = new ParkingLot(2, 2);
        int n = CsvStorage.load(loaded, file);
        check("2 tickets loaded", n == 2);
        check("1 vehicle still parked after load", loaded.getActiveTickets().size() == 1);
        check("bike slot occupied after load", loaded.availableSlots(VehicleType.BIKE) == 1);
        check("fee kept for closed ticket", loaded.searchByVehicle("C1").get(0).getFee() == 20);
        Ticket next = loaded.park(new Vehicle("C3", VehicleType.CAR));
        check("new ticket id continues (3)", next != null && next.getId() == 3);
        new java.io.File(file).delete();
    }

    static void check(String name, boolean ok) {
        if (ok) {
            passed++;
            System.out.println("PASS  " + name);
        } else {
            failed++;
            System.out.println("FAIL  " + name);
        }
    }
}
