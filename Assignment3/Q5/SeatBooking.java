package Q5;

/**
 * Q5 - Race Conditions in a Cinema Seat-Booking System
 * Complete the Code: SeatBooking.java
 * Student: Moksha Dude (192372374)
 */
public class SeatBooking {
    private final boolean[] booked;
    private int bookingsMade = 0;

    SeatBooking(int seats) {
        booked = new boolean[seats];
    }

    // TODO 1: Make the check-then-book operation atomic so that a seat can never be sold twice
    public synchronized boolean book(int seat) {
        if (!booked[seat]) {
            // Simulated check-then-act window
            booked[seat] = true;
            bookingsMade++;
            return true;
        }
        return false;
    }

    public int getBookingsMade() {
        return bookingsMade;
    }

    public static void main(String[] args) throws InterruptedException {
        SeatBooking screen = new SeatBooking(10);
        Thread[] users = new Thread[100];
        for (int i = 0; i < users.length; i++) {
            final int seat = i % 10; // 10 users compete for every seat
            users[i] = new Thread(() -> screen.book(seat));
            users[i].start();
        }
        for (Thread t : users) t.join();
        System.out.println("Seats sold: " + screen.bookingsMade);

        System.out.println("\n--- Race Condition Analysis ---");
        System.out.println("In the unsynchronized version, Thread A checks `!booked[seat]` and finds it false.");
        System.out.println("Before Thread A sets `booked[seat] = true`, the OS context-switches to Thread B.");
        System.out.println("Thread B also observes `!booked[seat] == false` and books the seat.");
        System.out.println("Both threads execute `bookingsMade++` (which itself is a non-atomic read-modify-write),");
        System.out.println("resulting in over-allocation where the same seat is double-sold to multiple patrons.");
        System.out.println("Using the synchronized method enforces mutual exclusion on the instance's intrinsic monitor lock,");
        System.out.println("guaranteeing strict serializability across check-and-set.");
    }
}
