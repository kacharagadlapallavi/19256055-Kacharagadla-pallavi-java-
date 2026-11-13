class Vehicle {
    String regNo;
    double rate;

    Vehicle(String regNo) {
        this.regNo = regNo;
        this.rate = 10;
        System.out.println("Vehicle constructor");
    }

    double calculateFee(int minutes) {
        int hours = minutes / 60;
        if (minutes % 60 != 0) {
            hours++;
        }
        return hours * rate;
    }
}

class Car extends Vehicle {
    Car(String regNo) {
        super(regNo);
        this.rate = 30;
        System.out.println("Car constructor");
    }
}

class ElectricCar extends Car {
    int battery;

    ElectricCar(String regNo, int battery) {
        super(regNo);
        this.battery = battery;
        System.out.println("ElectricCar constructor");
    }

    double chargingFee(int kWh) {
        return kWh * 12;
    }
}

class User {
    String name;

    User(String name) {
        this.name = name;
    }

    boolean freeParking() {
        return false;
    }
}

class Student extends User {
    Student(String name) {
        super(name);
    }

    boolean freeParking() {
        return true;
    }
}

class Visitor extends User {
    Visitor(String name) {
        super(name);
    }
}

class VehicleType {
    String name;
    double rate;

    VehicleType(String name, double rate) {
        this.name = name;
        this.rate = rate;
    }
}

class VehicleC {
    String regNo;
    VehicleType type;

    VehicleC(String regNo, VehicleType type) {
        this.regNo = regNo;
        this.type = type;
    }

    double calculateFee(int minutes) {
        int hours = minutes / 60;
        if (minutes % 60 != 0) {
            hours++;
        }
        return hours * type.rate;
    }
}

public class Task6 {
    public static void main(String[] args) {
        ElectricCar ev = new ElectricCar("TN03C3", 40);
        System.out.println(ev.calculateFee(130) + ev.chargingFee(10));

        Student s = new Student("Asha");
        Visitor v = new Visitor("Guest");
        System.out.println(s.name + " " + s.freeParking());
        System.out.println(v.name + " " + v.freeParking());

        VehicleC bike = new VehicleC("TN09Z9", new VehicleType("EBike", 5));
        System.out.println(bike.calculateFee(130));
    }
}
