package parking.app;

import parking.model.ParkingSlot;
import parking.service.ParkingService;

public class Main {

    public static void main(String[] args) {

        ParkingSlot slot = new ParkingSlot(101);

        ParkingService service = new ParkingService();

        service.updateDuration(slot, 5);

        service.displaySlot(slot);
    }
}
