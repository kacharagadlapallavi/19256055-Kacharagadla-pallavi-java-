import java.util.ArrayList;
import java.util.Scanner;

public class Task2 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter vehicle number: ");
        String vehicleNo = sc.next();
        System.out.print("Enter minutes parked: ");
        int minutes = sc.nextInt();

        int slotNumber = 5;
        double rate = 30.0;

        int wrongHours = minutes / 60;
        double wrongFee = wrongHours * rate;

        int hours = minutes / 60;
        if (minutes % 60 != 0) {
            hours++;
        }
        double fee = hours * rate;

        System.out.println("Vehicle " + vehicleNo + " slot " + slotNumber);
        System.out.println("Wrong fee: " + wrongFee);
        System.out.println("Correct fee: " + fee);

        double price = 12.5;
        int wrongPrice = (int) price;
        System.out.println("Cast loses decimals: " + wrongPrice
                + " instead of " + price);

        int[] slotsPerFloor = {20, 50, 100};
        String[][] slots = new String[3][];
        for (int f = 0; f < 3; f++) {
            slots[f] = new String[slotsPerFloor[f]];
        }
        slots[1][5] = vehicleNo;
        System.out.println(slots[1][5]);

        String[] oneD = new String[170];
        int floor = 1;
        int slot = 5;
        int index = 20 + slot;
        oneD[index] = vehicleNo;
        System.out.println(oneD[index]);

        ArrayList<String[]> floors = new ArrayList<String[]>();
        floors.add(new String[20]);
        floors.add(new String[50]);
        floors.add(new String[100]);
        floors.get(floor)[slot] = vehicleNo;
        System.out.println(floors.get(floor)[slot]);
    }
}
