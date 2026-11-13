class Vehicle {
    String regNo;
    String type;
    int entryTime;

    Vehicle(String regNo, String type, int entryTime) {
        this.regNo = regNo;
        this.type = type;
        this.entryTime = entryTime;
    }
}

class ParkingSlot {
    int slotNumber;
    boolean occupied;

    ParkingSlot(int slotNumber) {
        this.slotNumber = slotNumber;
        this.occupied = false;
    }
}

class ParkingTicket {
    static int count = 0;
    int ticketNo;
    String regNo;
    int slotNumber;

    ParkingTicket(String regNo, int slotNumber) {
        count++;
        this.ticketNo = count;
        this.regNo = regNo;
        this.slotNumber = slotNumber;
    }
}

public class Task1 {
    public static void main(String[] args) {
        Vehicle[] vehicles = new Vehicle[3];
        vehicles[0] = new Vehicle("TN01AB1111", "Car", 600);
        vehicles[1] = new Vehicle("TN02CD2222", "TwoWheeler", 615);
        vehicles[2] = new Vehicle("TN03EF3333", "EV", 630);

        ParkingSlot slot = new ParkingSlot(1);
        slot.occupied = true;
        ParkingTicket ticket = new ParkingTicket(vehicles[0].regNo,
                slot.slotNumber);
        System.out.println("Ticket " + ticket.ticketNo + " slot "
                + ticket.slotNumber);

        String search = "TN03EF3333";
        for (int i = 0; i < vehicles.length; i++) {
            if (vehicles[i].regNo.equals(search)) {
                System.out.println("Found: " + vehicles[i].type + " "
                        + vehicles[i].entryTime);
            }
        }

        String[] regNo = {"TN01AB1111", "TN02CD2222", "TN03EF3333"};
        String[] type = {"Car", "TwoWheeler", "EV"};
        int[] entryTime = {600, 615, 630};
        for (int i = 0; i < regNo.length; i++) {
            if (regNo[i].equals(search)) {
                System.out.println("Found: " + type[i] + " " + entryTime[i]);
            }
        }
    }
}
