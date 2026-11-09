interface Chargeable {
    double chargingFee(int kWh);
}

interface Payable {
    double amountDue();
}

abstract class Vehicle {
    String regNo;

    Vehicle(String regNo) {
        this.regNo = regNo;
    }

    abstract double ratePerHour();

    double calculateFee(int minutes) {
        int hours = minutes / 60;
        if (minutes % 60 != 0) {
            hours++;
        }
        return hours * ratePerHour();
    }
}

class ElectricCar extends Vehicle implements Chargeable, Payable {
    ElectricCar(String regNo) {
        super(regNo);
    }

    double ratePerHour() {
        return 30;
    }

    public double chargingFee(int kWh) {
        return kWh * 12;
    }

    public double amountDue() {
        return calculateFee(130) + chargingFee(10);
    }
}

interface VehicleI {
    double ratePerHour();
}

abstract class ChargeableA {
    abstract double chargingFee(int kWh);
}

class ElectricCar2 extends ChargeableA implements VehicleI {
    public double ratePerHour() {
        return 30;
    }

    double chargingFee(int kWh) {
        return kWh * 12;
    }
}

public class Task9 {
    public static void main(String[] args) {
        ElectricCar ev = new ElectricCar("TN03C3");
        System.out.println("Amount due: " + ev.amountDue());

        ElectricCar2 ev2 = new ElectricCar2();
        System.out.println(ev2.ratePerHour() + " " + ev2.chargingFee(10));
    }
}
