package environment;
import mainObjects.*;

import java.util.ArrayList;
import java.util.Objects;

public class Fireplace extends LightSource{
    public final ArrayList<Log> logs = new ArrayList<>();
    public final Position pos;

    public Fireplace(Position pos) {
        this.isOn = false;
        this.intensity = 0;
        this.pos = pos;
    }
    public void addLog(Log obj) {
        logs.add(obj);
        isOn = true;
        //добавили бревно -> увеличили интесивность света
        intensity = Math.min(100, intensity + 25);
    }

    @Override
    public void turnOn() {
        isOn = true;
        System.out.println("Камин зажжён:");
    }

    @Override
    public void turnOff() {
        isOn = false;
        intensity = 0;
        System.out.println("Камин потух:");
    }

    public void burnStep() {
        // если брёвна ещё есть в камине
        if (!logs.isEmpty()) {
            logs.remove(logs.size() - 1);
            System.out.println("Полено прогорело! Осталось: " + logs.size());
        }
        if (logs.isEmpty()) {
            turnOff();
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(isOn, intensity, logs);
    }

    @Override
    public String toString() {
        return "Fireplace{isOn=" + isOn +  ", intensity=" + intensity + ", logs=" + logs + "}";
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        Fireplace other = (Fireplace) object;
        return (this.isOn == other.isOn) && (this.intensity == other.intensity) && Objects.equals(this.logs, other.logs);
    }
}
