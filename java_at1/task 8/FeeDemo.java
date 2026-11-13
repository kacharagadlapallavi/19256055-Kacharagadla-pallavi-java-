interface Chargeable {
    double chargingFee(int kWh);
}

abstract class Vehicle {
    String regNo;
    String type;

    Vehicle(String regNo) {
        this.regNo = regNo;
    }

    abstract double ratePerHour();

    double calculateFee(int minutes) {
        int hours = (minutes + 59) / 60;
        return hours * ratePerHour();
    }

    public boolean equals(Object o) {
        Vehicle v = (Vehicle) o;
        return regNo.equals(v.regNo);
    }

    public int hashCode() {
        return regNo.hashCode();
    }
}

class TwoWheeler extends Vehicle {
    TwoWheeler(String regNo) {
        super(regNo);
        type = "TwoWheeler";
    }

    double ratePerHour() {
        return 10;
    }
}

class Car extends Vehicle {
    Car(String regNo) {
        super(regNo);
        type = "Car";
    }

    double ratePerHour() {
        return 30;
    }
}

class ElectricCar extends Car implements Chargeable {
    ElectricCar(String regNo) {
        super(regNo);
        type = "ElectricCar";
    }

    public double chargingFee(int kWh) {
        return kWh * 12;
    }
}

public class FeeDemo {
    public static void main(String[] args) {
        ElectricCar ev = new ElectricCar("TN03C3");
        Vehicle[] parked = new Vehicle[3];
        parked[0] = new TwoWheeler("TN01A1");
        parked[1] = new Car("TN02B2");
        parked[2] = ev;

        for (int i = 0; i < parked.length; i++) {
            double fee = parked[i].calculateFee(130);
            if (i == 2) {
                fee = fee + ev.chargingFee(10);
            }
            System.out.print(parked[i].type + " " + parked[i].regNo);
            System.out.println(" -> Rs " + fee);
        }

        Car c = new Car("TN02B2");
        ElectricCar e = new ElectricCar("TN02B2");
        System.out.println(c.equals(e));
    }
}
