class Vehicle {
    String regNo;

    Vehicle(String regNo) {
        this.regNo = regNo;
    }

    double calculateFee(int minutes) {
        int hours = minutes / 60;
        if (minutes % 60 != 0) {
            hours++;
        }
        return hours * 10;
    }

    public String toString() {
        return "Vehicle " + regNo;
    }

    public boolean equals(Object o) {
        Vehicle v = (Vehicle) o;
        return regNo.equals(v.regNo);
    }

    public int hashCode() {
        return regNo.hashCode();
    }
}

class Car extends Vehicle {
    Car(String regNo) {
        super(regNo);
    }

    double calculateFee(int minutes) {
        int hours = minutes / 60;
        if (minutes % 60 != 0) {
            hours++;
        }
        return hours * 30;
    }

    public String toString() {
        return "Car " + regNo;
    }
}

class TypeVehicle {
    String regNo;
    String type;

    TypeVehicle(String regNo, String type) {
        this.regNo = regNo;
        this.type = type;
    }

    double calculateFee(int minutes) {
        int hours = minutes / 60;
        if (minutes % 60 != 0) {
            hours++;
        }
        if (type.equals("TwoWheeler")) {
            return hours * 10;
        } else if (type.equals("Car")) {
            return hours * 30;
        }
        return 0;
    }
}

public class Task7 {
    public static void main(String[] args) {
        final int MAX_HOURS = 8;
        Vehicle a = new Vehicle("TN01A1");
        Vehicle b = new Car("TN02B2");
        System.out.println(a + " " + a.calculateFee(130));
        System.out.println(b + " " + b.calculateFee(130));
        System.out.println(new Vehicle("TN05X5").equals(new Car("TN05X5")));
        System.out.println(MAX_HOURS);

        TypeVehicle t = new TypeVehicle("TN02B2", "Car");
        System.out.println(t.calculateFee(130));
    }
}
