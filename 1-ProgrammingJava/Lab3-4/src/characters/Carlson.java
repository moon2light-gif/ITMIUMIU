package characters;
import java.util.Objects;
import mainObjects.*;
import environment.*;

public class Carlson extends MainCharacter {
    public Carlson(String name, String hairColor, int age, Position pos) {
        super(name, hairColor, age, pos);
    }
    //подкидываем два бревна
    public void addTwoLogs(Fireplace fp) {
        // используется как раз переопределённый toString()
        System.out.println("Камин до: " + fp.logs);
        fp.addLog(new Log(2.5, WoodType.BIRCH));
        fp.addLog(new Log(2.5, WoodType.BIRCH));
        System.out.println(this.name + " встал и подбросил в огонь два больших берёзовых полена");
        System.out.println("Камин после: " + fp.logs);
        Voice("Ох, что-то я перетрудился, надо потом тяпнуть варенья");
    }

    @Override
    public void Move(Room room) {
        System.out.println("Текущая позициция Карлсона: " + this.pos.x() + ", " + this.pos.y() + ", " +  this.pos.z());
        this.pos = room.getFireplace().pos;
        System.out.println("Карлсон встал и подошёл к камину, теперь его позиция: " + this.pos.x() + ", " + this.pos.y() + ", " + this.pos.z());
    }

    @Override
    public void lightLamp(Lamp lamp) {
        try {
            lamp.turnOn();
            Voice("Ну вот, так гораздо лучше, с таким освещением не грех и ещё раз тяпнуть варенья!");
        }
        catch(NoFuelException ex) {
            Voice(ex.getMessage());
        }
    }

    @Override
    public void Voice(String text) {
        System.out.println(name + ": - " + text);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || this.getClass() != object.getClass()) return false;
        Carlson other = (Carlson) object;
        return (this.age == other.age) && Objects.equals(this.name, other.name) && Objects.equals(this.hairColor, other.hairColor);
    }
    @Override
    public int hashCode() {
        return Objects.hash(name, hairColor, age);
    }

    @Override
    public String toString() {
        return "Carlson{name=" + name + ", hair=" + hairColor + ", age=" + age + "}";
    }

}
