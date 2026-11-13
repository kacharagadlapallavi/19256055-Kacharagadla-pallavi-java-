class PVehicle {
    double ratePerHour() {
        return 10;
    }

    double calculateFee(int minutes) {
        int hours = minutes / 60;
        if (minutes % 60 != 0) {
            hours++;
        }
        return hours * ratePerHour();
    }

    double calculateFee(int inHour, int outHour) {
        return calculateFee((outHour - inHour) * 60);
    }
}

class PBike extends PVehicle {
    double ratePerHour() {
        return 10;
    }
}

class PCar extends PVehicle {
    double ratePerHour() {
        return 30;
    }
}

public class Task8Poly {
    public static void main(String[] args) {
        PVehicle[] vehicles = new PVehicle[2];
        vehicles[0] = new PBike();
        vehicles[1] = new PCar();

        for (int i = 0; i < vehicles.length; i++) {
            System.out.println(vehicles[i].calculateFee(130));
        }

        System.out.println(vehicles[1].calculateFee(9, 11));

        for (int i = 0; i < vehicles.length; i++) {
            if (vehicles[i] instanceof PCar) {
                System.out.println("PCar fee " + vehicles[i].calculateFee(130));
            }
            if (vehicles[i] instanceof PBike) {
                System.out.println("PBike fee " + vehicles[i].calculateFee(130));
            }
        }
    }
}
