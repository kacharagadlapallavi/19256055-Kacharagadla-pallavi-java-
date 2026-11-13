class EBike extends Vehicle {
    EBike(String regNo) {
        super(regNo, 0);
    }

    double calculateFee(int minutes) {
        if (minutes <= 0) {
            return 0;
        }
        return 15;
    }
}

class DeliveryVan extends Vehicle {
    int entryHour;

    DeliveryVan(String regNo, int entryHour) {
        super(regNo, 30);
        this.entryHour = entryHour;
    }

    double calculateFee(int minutes) {
        double fee = super.calculateFee(minutes);
        if (entryHour >= 9 && entryHour < 17) {
            fee = fee * 2;
        }
        return fee;
    }
}

class OneDayPass {
    int day;

    OneDayPass(int day) {
        this.day = day;
    }

    boolean isValid(int today) {
        return day == today;
    }
}

public class Task12 {
    public static void main(String[] args) {
        int[] floors_lot = {20, 35, 50, 40, 60};
        ParkingLot lot = new ParkingLot(floors_lot);
        int capacity = 0;
        for (int f = 0; f < lot.slots.length; f++) {
            capacity = capacity + lot.slots[f].length;
        }
        System.out.println("Test 1 expected 205 actual " + capacity);

        lot.enter(new EBike("EB1"), 600);
        System.out.println("Test 2 expected 15.0 actual "
                + lot.exit("EB1", 780, false, 0));

        lot.enter(new DeliveryVan("DV1", 10), 600);
        System.out.println("Test 3 expected 120.0 actual "
                + lot.exit("DV1", 720, false, 0));

        lot.enter(new DeliveryVan("DV2", 18), 1080);
        System.out.println("Test 4 expected 60.0 actual "
                + lot.exit("DV2", 1200, false, 0));

        lot.enter(new DeliveryVan("DV3", 17), 1020);
        System.out.println("Test 5 expected 60.0 actual "
                + lot.exit("DV3", 1140, false, 0));

        OneDayPass pass = new OneDayPass(27);
        lot.enter(new Car("V1"), 600);
        System.out.println("Test 6 expected 0.0 actual "
                + lot.exit("V1", 900, pass.isValid(27), 0));

        lot.enter(new Car("V2"), 600);
        System.out.println("Test 7 expected 60.0 actual "
                + lot.exit("V2", 720, pass.isValid(28), 0));

        lot.enter(new ElectricCar("EV1"), 0);
        System.out.println("Test 8 expected 180.0 actual "
                + lot.exit("EV1", 90, false, 10));

        lot.enter(new TwoWheeler("TW1"), 0);
        System.out.println("Test 9 expected 30.0 actual "
                + lot.exit("TW1", 130, false, 0));

        System.out.println("Test 10 expected 525.0 actual " + lot.collection);
    }
}
