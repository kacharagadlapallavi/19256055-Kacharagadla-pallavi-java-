package Q6;

/**
 * Q6 Part A - Deadlock and Prevention in Hostel Wallet Transfers
 * Demonstrating circular wait and preventing deadlock via global lock ordering.
 */
public class DeadlockDemo {

    static class Wallet {
        final int id;
        double balance;

        Wallet(int id, double balance) {
            this.id = id;
            this.balance = balance;
        }
    }

    // Deadlock-prone transfer (locks from then to)
    public static void unsafeTransfer(Wallet from, Wallet to, double amount) {
        synchronized (from) {
            try { Thread.sleep(20); } catch (InterruptedException ignored) {}
            synchronized (to) {
                from.balance -= amount;
                to.balance += amount;
            }
        }
    }

    // Safe transfer using strict ID lock ordering to eliminate Circular Wait
    public static void safeTransfer(Wallet w1, Wallet w2, double amount) {
        Wallet first = w1.id < w2.id ? w1 : w2;
        Wallet second = w1.id < w2.id ? w2 : w1;

        synchronized (first) {
            synchronized (second) {
                w1.balance -= amount;
                w2.balance += amount;
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Q6 Part A: Deadlock Prevention via Global Lock Ordering ===");
        Wallet wallet1 = new Wallet(101, 5000);
        Wallet wallet2 = new Wallet(102, 5000);

        System.out.println("Testing safe transfer with competing threads...");
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 500; i++) safeTransfer(wallet1, wallet2, 5);
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 500; i++) safeTransfer(wallet2, wallet1, 5);
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Transfers completed successfully without deadlock!");
        System.out.printf("Final Balances: Wallet1=Rs. %.2f, Wallet2=Rs. %.2f (Total constant at Rs. 10,000)\n",
                wallet1.balance, wallet2.balance);
        System.out.println("Four Coffman Deadlock Conditions eliminated: Circular Wait broken by total resource ordering.");
    }
}
