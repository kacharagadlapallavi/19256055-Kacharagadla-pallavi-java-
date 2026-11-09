public class FastFacility {
    String[][] slots;
    int[] freeCount;
    int[][] freeSlots;
    int[] top;

    public FastFacility(int[] slotsPerFloor) {
        slots = new String[slotsPerFloor.length][];
        freeCount = new int[slotsPerFloor.length];
        freeSlots = new int[slotsPerFloor.length][];
        top = new int[slotsPerFloor.length];
        for (int f = 0; f < slotsPerFloor.length; f++) {
            int size = slotsPerFloor[f];
            slots[f] = new String[size];
            freeSlots[f] = new int[size];
            freeCount[f] = size;
            top[f] = size;
            for (int i = 0; i < size; i++) {
                freeSlots[f][i] = size - 1 - i;
            }
        }
    }

    public int[] allocate(String vehicleNo) {
        for (int f = 0; f < slots.length; f++) {
            if (freeCount[f] > 0) {
                top[f]--;
                int s = freeSlots[f][top[f]];
                slots[f][s] = vehicleNo;
                freeCount[f]--;
                int[] result = new int[2];
                result[0] = f;
                result[1] = s;
                return result;
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
        freeSlots[floor][top[floor]] = slot;
        top[floor]++;
        freeCount[floor]++;
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
        FastFacility p = new FastFacility(floors);
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
