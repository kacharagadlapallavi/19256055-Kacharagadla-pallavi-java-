package parking.model;

public class ParkingSlot {

    private int slotNumber;
    private Integer duration;
    private boolean occupied;

    public ParkingSlot(int slotNumber) {
        setSlotNumber(slotNumber);
        this.duration = 0;
        this.occupied = false;
    }

    // Getter
    public int getSlotNumber() {
        return slotNumber;
    }

    // Validating setter
    public void setSlotNumber(int slotNumber) {
        if (slotNumber <= 0) {
            throw new IllegalArgumentException(
                "Invalid slot number"
            );
        }
        this.slotNumber = slotNumber;
    }

    public Integer getDuration() {
        return duration;
    }

    // Validating setter
    public void setDuration(Integer duration) {
        if (duration == null || duration < 0) {
            throw new IllegalArgumentException(
                "Duration cannot be negative or null"
            );
        }
        this.duration = duration;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }
}
