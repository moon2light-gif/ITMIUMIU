package environment;
import mainObjects.*;

import java.io.ObjectStreamException;
import java.util.Objects;

public class Lamp extends LightSource {
    private final double fuel;
    private final Position pos;

    public Lamp(double fuel, Position pos) {
        this.fuel = fuel;
        this.pos = pos;
    }

    @Override
    public void turnOn() throws NoFuelException {
        if (fuel == 0) {
            System.out.println("Керосиновая лампа не горит: ");
            throw new NoFuelException();
        }
        else {
            isOn = true;
            intensity = 70;
            System.out.println("Керосиновая лампа зажжена:");
        }
    }

    @Override
    public void turnOff() {
        isOn = false;
        intensity = 0;
        System.out.println("Керосиновая лампа выключена:");
    }

    @Override
    public int hashCode() {
        return Objects.hash(fuel, pos);
    }

    @Override
    public String toString() {
        return "Lamp{fuel=" + fuel + ", pos=" + pos + "}";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || this.getClass() != object.getClass()) return false;
        Lamp other = (Lamp) object;
        return this.fuel == other.fuel && Objects.equals(this.pos, other.pos);
    }
}
