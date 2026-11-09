class Ticket {
    static int counter;
    static int occupancy;
    int ticketNo;

    static {
        counter = 100;
        occupancy = 0;
    }

    Ticket() {
        counter++;
        ticketNo = counter;
        occupancy++;
    }

    static void leave() {
        occupancy--;
    }
}

class Facility {
    int counter = 0;
    String name;

    Facility(String name) {
        this.name = name;
    }

    int issueTicket() {
        counter++;
        return counter;
    }
}

class SharedFacility {
    static int counter = 0;
    String name;

    SharedFacility(String name) {
        this.name = name;
    }

    int issueTicket() {
        counter++;
        return counter;
    }
}

public class Task5 {
    static Ticket pass(Ticket t) {
        return t;
    }

    public static void main(String[] args) {
        Ticket a = new Ticket();
        Ticket b = new Ticket();
        System.out.println(a.ticketNo + " " + b.ticketNo);
        System.out.println("Occupancy: " + Ticket.occupancy);

        Ticket c = pass(a);
        System.out.println(c == a);

        Ticket.leave();
        a = null;
        c = null;
        System.gc();
        System.out.println("Occupancy: " + Ticket.occupancy);

        Facility north = new Facility("North");
        Facility south = new Facility("South");
        System.out.println(north.issueTicket() + " " + south.issueTicket());

        SharedFacility n = new SharedFacility("North");
        SharedFacility s = new SharedFacility("South");
        System.out.println(n.issueTicket() + " " + s.issueTicket());
    }
}
