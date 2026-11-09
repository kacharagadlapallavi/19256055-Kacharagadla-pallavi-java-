package parking.service;

import parking.model.ParkingSlot;

public class ParkingService {

    public void displaySlot(ParkingSlot slot) {

        System.out.println("Slot Number: "
                + slot.getSlotNumber());

        System.out.println("Duration: "
                + slot.getDuration());

        System.out.println("Occupied: "
                + slot.isOccupied());
    }

    public void updateDuration(
            ParkingSlot slot, int duration) {

        slot.setDuration(duration);
        System.out.println("Duration updated successfully");
    }
}
