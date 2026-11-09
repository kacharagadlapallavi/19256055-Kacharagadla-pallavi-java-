class ParkingTicket {
    static int count = 0;
    int ticketNo;
    String regNo;
    int floor;
    int slot;

    ParkingTicket() {
        this("UNKNOWN");
    }

    ParkingTicket(String regNo) {
        this(regNo, 0, 0);
    }

    ParkingTicket(String regNo, int floor, int slot) {
        count++;
        this.ticketNo = count;
        this.regNo = regNo;
        this.floor = floor;
        this.slot = slot;
    }

    ParkingTicket(ParkingTicket t) {
        this.ticketNo = t.ticketNo;
        this.regNo = t.regNo;
        this.floor = t.floor;
        this.slot = t.slot;
    }

    static ParkingTicket create(String regNo, int floor, int slot) {
        return new ParkingTicket(regNo, floor, slot);
    }

    void show() {
        System.out.println(ticketNo + " " + regNo + " " + floor + " " + slot);
    }
}

class SetterTicket {
    int ticketNo;
    String regNo;
    int floor;
    int slot;

    void setTicketNo(int ticketNo) {
        this.ticketNo = ticketNo;
    }

    void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    void setFloor(int floor) {
        this.floor = floor;
    }

    void setSlot(int slot) {
        this.slot = slot;
    }
}

public class Task4 {
    public static void main(String[] args) {
        ParkingTicket a = new ParkingTicket();
        ParkingTicket b = new ParkingTicket("TN01AB1111");
        ParkingTicket c = new ParkingTicket("TN02CD2222", 1, 4);
        ParkingTicket d = new ParkingTicket(c);
        ParkingTicket e = ParkingTicket.create("TN03EF3333", 2, 9);
        a.show();
        b.show();
        c.show();
        d.show();
        e.show();

        SetterTicket s = new SetterTicket();
        s.setTicketNo(10);
        s.setRegNo("TN04GH4444");
        s.setFloor(0);
        s.setSlot(3);
        System.out.println(s.ticketNo + " " + s.regNo + " " + s.floor + " "
                + s.slot);
    }
}
