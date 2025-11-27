package environment;
import mainObjects.*;
import java.util.*;

public class Room {
    private final Workbench workbench;
    private final ArrayList<LightSource> lights = new ArrayList<>();

    // конструктор для комнаты
    public Room(Workbench wrkbnch) {
        this.workbench = wrkbnch;
    }

    public void addLight(LightSource ls) {
        lights.add(ls);
    }

    // метод для получения камина
    public Fireplace getFireplace() {
        for (LightSource elem : lights) {
            if (elem instanceof Fireplace fireplace) {
                return fireplace;
            }
        }
        return null;
    }

    public void illuminateItems() {
        for (Item item : workbench.getItems()) {
            System.out.print("предмет " + item.getName() + ": ");
            item.illuminate();
            System.out.print(", ");

        }
        System.out.println();
    }

    public void hideItems() {
        for (Item item : workbench.getItems()) {
            System.out.print("предмет " + item.getName() + ": ");
            item.hide();
            System.out.print(", ");
        }
        System.out.println();
    }

    // указывает на новый уровень освещённости
    public void updateLight() {
        LightLevel levelOfLight = LightLevel.OBSCURITY;
        for (LightSource elem : lights) {
            if (elem.isOn) {
                // получаем уровень освещенности
                levelOfLight = elem.getLightLevel();
            }
        }
        System.out.println("Текущий уровень освещённости комнаты: " + levelOfLight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workbench, lights);
    }

    @Override
    public String toString() {
        return "Room{workbench=" + workbench + ", lights in room=" + lights + "}";
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || this.getClass() != object.getClass()) return false;

        Room other = (Room) object;
        return Objects.equals(this.lights, other.lights) && Objects.equals(this.workbench, other.workbench);
    }
}
