import java.util.ArrayList;
import java.util.HashMap;

class Vehicle {
    String regNo;
    double rate;

    Vehicle(String regNo, double rate) {
        this.regNo = regNo;
        this.rate = rate;
    }

    double calculateFee(int minutes) {
        int hours = minutes / 60;
        if (minutes % 60 != 0) {
            hours++;
        }
        double fee = hours * rate;
        if (fee > rate * 8) {
            fee = rate * 8;
        }
        return fee;
    }

    double chargingFee(int kWh) {
        return 0;
    }
}

class TwoWheeler extends Vehicle {
    TwoWheeler(String regNo) {
        super(regNo, 10);
    }
}

class Car extends Vehicle {
    Car(String regNo) {
        super(regNo, 30);
    }
}

class ElectricCar extends Car {
    ElectricCar(String regNo) {
        super(regNo);
    }

    double chargingFee(int kWh) {
        return kWh * 12;
    }
}

class Ticket {
    static int count = 0;
    int ticketNo;
    Vehicle vehicle;
    int floor;
    int slot;
    int entryMinute;

    Ticket(Vehicle vehicle, int floor, int slot, int entryMinute) {
        count++;
        this.ticketNo = count;
        this.vehicle = vehicle;
        this.floor = floor;
        this.slot = slot;
        this.entryMinute = entryMinute;
    }
}

class ParkingLot {
    String[][] slots;
    Ticket[] tickets = new Ticket[100];
    int ticketCount = 0;
    double collection = 0;

    ParkingLot(int[] slotsPerFloor) {
        slots = new String[slotsPerFloor.length][];
        for (int f = 0; f < slotsPerFloor.length; f++) {
            slots[f] = new String[slotsPerFloor[f]];
        }
    }

    int findTicket(String regNo) {
        for (int i = 0; i < ticketCount; i++) {
            if (tickets[i].vehicle.regNo.equals(regNo)) {
                return i;
            }
        }
        return -1;
    }

    Ticket enter(Vehicle v, int entryMinute) {
        if (findTicket(v.regNo) != -1) {
            System.out.println("Duplicate registration number");
            return null;
        }
        for (int f = 0; f < slots.length; f++) {
            for (int s = 0; s < slots[f].length; s++) {
                if (slots[f][s] == null) {
                    slots[f][s] = v.regNo;
                    Ticket t = new Ticket(v, f, s, entryMinute);
                    tickets[ticketCount] = t;
                    ticketCount++;
                    return t;
                }
            }
        }
        System.out.println("Parking full");
        return null;
    }

    double exit(String regNo, int exitMinute, boolean hasPass, int kWh) {
        int i = findTicket(regNo);
        if (i == -1) {
            System.out.println("No ticket found");
            return -1;
        }
        Ticket t = tickets[i];
        slots[t.floor][t.slot] = null;
        for (int j = i; j < ticketCount - 1; j++) {
            tickets[j] = tickets[j + 1];
        }
        ticketCount--;

        double fee = 0;
        if (!hasPass) {
            fee = t.vehicle.calculateFee(exitMinute - t.entryMinute);
        }
        fee = fee + t.vehicle.chargingFee(kWh);
        collection = collection + fee;
        return fee;
    }

    boolean release(int floor, int slot) {
        if (floor < 0 || floor >= slots.length) {
            return false;
        }
        if (slot < 0 || slot >= slots[floor].length) {
            return false;
        }
        if (slots[floor][slot] == null) {
            return false;
        }
        slots[floor][slot] = null;
        return true;
    }
}

public class Task11 {
    static Vehicle makeVehicle(String type, String regNo) {
        if (type.equals("TWOWHEELER")) {
            return new TwoWheeler(regNo);
        } else if (type.equals("CAR")) {
            return new Car(regNo);
        } else if (type.equals("EV")) {
            return new ElectricCar(regNo);
        }
        System.out.println("Unknown vehicle type: " + type);
        return null;
    }

    public static void main(String[] args) {
        int[] floors_lot = {2, 1};
        ParkingLot lot = new ParkingLot(floors_lot);
        Ticket t1 = lot.enter(new TwoWheeler("TN01A1"), 0);
        Ticket t2 = lot.enter(new Car("TN02B2"), 0);
        Ticket t3 = lot.enter(new ElectricCar("TN03C3"), 0);
        System.out.println("Test 1 expected 0,0 actual " + t1.floor + ","
                + t1.slot);
        System.out.println("Test 2 expected 1,0 actual " + t3.floor + ","
                + t3.slot);
        System.out.println("Test 3 expected true actual "
                + (t1.ticketNo != t2.ticketNo));

        System.out.println("Test 4 expected Parking full");
        lot.enter(new Car("TN04D4"), 0);

        System.out.println("Test 5 expected 30.0 actual "
                + lot.exit("TN01A1", 130, false, 0));
        System.out.println("Test 6 expected 0.0 actual "
                + lot.exit("TN02B2", 0, false, 0));
        System.out.println("Test 7 expected 180.0 actual "
                + lot.exit("TN03C3", 90, false, 10));

        Car c = new Car("X");
        System.out.println("Test 8 expected 30.0 actual " + c.calculateFee(60));
        System.out.println("Test 9 expected 60.0 actual " + c.calculateFee(61));
        System.out.println("Test 10 expected 240.0 actual " + c.calculateFee(1440));

        System.out.println("Test 11 expected Unknown vehicle type: BUS");
        makeVehicle("BUS", "TN05E5");

        int[] floors_lot2 = {2};
        ParkingLot lot2 = new ParkingLot(floors_lot2);
        lot2.enter(new Car("TN06F6"), 0);
        System.out.println("Test 12 expected Duplicate registration number");
        lot2.enter(new Car("TN06F6"), 0);
        System.out.println("Test 13 expected false actual " + lot2.release(0, 9));

        int[] floors_day = {2, 2};
        ParkingLot day = new ParkingLot(floors_day);
        day.enter(new TwoWheeler("A1"), 0);
        day.enter(new Car("B2"), 0);
        day.enter(new ElectricCar("C3"), 0);
        day.enter(new Car("D4"), 0);
        day.exit("A1", 130, false, 0);
        day.exit("B2", 60, false, 0);
        day.exit("C3", 90, false, 10);
        day.exit("D4", 300, true, 0);
        System.out.println("Test 14 expected 240.0 actual " + day.collection);

        ArrayList<Ticket> list = new ArrayList<Ticket>();
        HashMap<String, Ticket> map = new HashMap<String, Ticket>();
        for (int i = 0; i < 10000; i++) {
            Ticket t = new Ticket(new Car("TN" + i), 0, i, 0);
            list.add(t);
            map.put("TN" + i, t);
        }

        long start = System.nanoTime();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).vehicle.regNo.equals("TN9999")) {
                break;
            }
        }
        long end = System.nanoTime();
        System.out.println("ArrayList time: " + (end - start));

        start = System.nanoTime();
        map.get("TN9999");
        end = System.nanoTime();
        System.out.println("HashMap time: " + (end - start));
    }
}
