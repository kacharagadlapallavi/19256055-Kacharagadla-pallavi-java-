public class ParkingFacility {
    String[][] slots;

    public ParkingFacility(int[] slotsPerFloor) {
        slots = new String[slotsPerFloor.length][];
        for (int f = 0; f < slotsPerFloor.length; f++) {
            slots[f] = new String[slotsPerFloor[f]];
        }
    }

    public int[] allocate(String vehicleNo) {
        for (int f = 0; f < slots.length; f++) {
            for (int s = 0; s < slots[f].length; s++) {
                if (slots[f][s] == null) {
                    slots[f][s] = vehicleNo;
                    int[] result = new int[2];
                    result[0] = f;
                    result[1] = s;
                    return result;
                }
            }
        }
        return null;
    }

    public boolean release(int floor, int slot) {
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

    public static void show(int[] r) {
        if (r == null) {
            System.out.println("null");
        } else {
            System.out.println("[" + r[0] + ", " + r[1] + "]");
        }
    }

    public static void main(String[] args) {
        int[] floors = {2, 1};
        ParkingFacility p = new ParkingFacility(floors);
        show(p.allocate("TN01AB1111"));
        show(p.allocate("TN02CD2222"));
        show(p.allocate("TN03EF3333"));
        show(p.allocate("TN04GH4444"));
        boolean a = p.release(0, 1);
        boolean b = p.release(0, 1);
        boolean c = p.release(5, 0);
        System.out.println(a + " " + b + " " + c);
        show(p.allocate("TN04GH4444"));
    }
}
